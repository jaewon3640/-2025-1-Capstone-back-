package com.example.demo.service;

import com.example.demo.UserRole;
import com.example.demo.domain.CareRelation;
import com.example.demo.domain.FcmToken;
import com.example.demo.domain.Notification;
import com.example.demo.domain.User;
import com.example.demo.domain.schedule.Schedule;
import com.example.demo.domain.schedule.ScheduleType;
import com.example.demo.repository.*;
import com.google.firebase.messaging.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationScheduler {

    private final NotificationRepository notificationRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;
    private final CareRelationRepository careRelationRepository;
    private final ScheduleRepository scheduleRepository;
    private final TaskScheduler taskScheduler;


    // 피보호자 알림

    // 1. 건강 상태 입력 알림: 매일 오후 8시에 전송(모든 피보호자 기기)
    @Scheduled(cron = "0 0 20 * * *")
    public void sendHealthStatusCheckNotifications() throws FirebaseMessagingException {
        List<String> registrationTokens = fcmTokenRepository.findByRole(UserRole.피보호자).stream()
                .map(FcmToken::getToken)
                .toList();

        sendNotifications(registrationTokens,
                "건강 상태 알림", "오늘의 건강 상태를 입력해주세요.");
    }

    // 2. 일정 알림: 설정된 시간 10분(약 복용) / 1시간(병원) 전에 전송(해당 피보호자 기기)
    @Transactional
    public void saveUpcomingScheduleNotification(Schedule schedule) {
        String userToken = fcmTokenRepository.findByUserId(schedule.getProtectedUserId())
                .orElseThrow(() -> new RuntimeException("해당 사용자의 토큰이 존재하지 않습니다."))
                .getToken();

        Notification msg = Notification.builder()
                .role(UserRole.피보호자)
                .protectedUserId(schedule.getProtectedUserId())
                .token(userToken)
                .scheduleId(schedule.getId())
                .title(schedule.getTitle())
                .type(schedule.getType())
                .notifiedAt(schedule.getDateTime())
                .sent(false)
                .build();

        String msgTitle = (msg.getType() == ScheduleType.MEDICINE)
                ? msg.getTitle() + " 복용 알림" : msg.getTitle() + " 방문 알림";

        int hour = msg.getNotifiedAt().getHour();
        int minute = msg.getNotifiedAt().getMinute();
        String day = (hour < 12) ? "오전" : "오후";
        if (hour == 0) hour = 12;
        if (hour > 12) hour -= 12;

        String body = String.format("오늘 %s %02d시 %02d분에 %s%s", day, hour, minute, msg.getTitle(),
                (msg.getType() == ScheduleType.MEDICINE) ? "(을/를) 복용하셔야 합니다." : " 방문 일정이 있습니다.");
        log.info("title: {}, body: {}.", msgTitle, body);

        msg = msg.toBuilder()
                .msgTitle(msgTitle)
                .body(body)
                .build();

        notificationRepository.save(msg);
        setUpcomingScheduleNotification(msg);
    }

    public void setUpcomingScheduleNotification(Notification msg) {
        LocalDateTime dateTime = (msg.getType() == ScheduleType.MEDICINE)
                ? msg.getNotifiedAt().minusMinutes(10)
                : msg.getNotifiedAt().minusHours(1);
        taskScheduler.schedule(() -> {
            try {
                sendNotification(msg);
            } catch (FirebaseMessagingException e) {
                throw new RuntimeException(e);
            }
        }, dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }


    // 보호자 알림

    // 1. 건강 상태 확인 알림: 피보호자가 건강 상태 입력 완료하면 전송(해당 보호자 기기)
    public void sendHealthStatusNotifications(Long protectedUserId) throws FirebaseMessagingException {
        User protectedUser = userRepository.findById(protectedUserId)
                .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));

        List<String> caregiverTokens = getCaregiverTokens(protectedUserId);
        String body = protectedUser.getName() + "님이 오늘의 건강 상태를 입력하셨습니다.";
        sendNotifications(caregiverTokens, "건강 상태 확인 알림", body);
    }

    private List<String> getCaregiverTokens(Long protectedUserId) {
        List<Long> caregivers = careRelationRepository
                .findByProtectedUserId(protectedUserId).stream()
                .map(CareRelation::getCaregiver)
                .map(User::getId)
                .toList();
        if (caregivers.isEmpty()) {
            throw new RuntimeException("해당 사용자의 보호자가 존재하지 않습니다.");
        }

        return caregivers.stream()
                .map(caregiverId -> fcmTokenRepository.findByUserId(caregiverId)
                        .orElseThrow(() -> new RuntimeException("해당 사용자의 토큰이 존재하지 않습니다.")))
                .map(FcmToken::getToken)
                .toList();
    }

    // 2. 약 복용 일정 미완료 알림: 설정된 시간 10분 후에 전송(해당 보호자 기기)
    public void setMissedScheduleNotification(Schedule schedule) {
        LocalDateTime dateTime = schedule.getDateTime().plusMinutes(10);
        taskScheduler.schedule(() -> sendMissedScheduleNotification(schedule),
                dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public void sendMissedScheduleNotification(Schedule schedule) {
        boolean completed = scheduleRepository.findById(schedule.getId())
                .orElseThrow(() -> new RuntimeException("해당 일정이 존재하지 않습니다."))
                .isCompleted();
        if (!completed) {
            List<Notification> msgList = saveMissedScheduleNotification(schedule);
            try {
                sendNotifications(msgList.get(0));
            } catch (FirebaseMessagingException e) {
                throw new RuntimeException(e);
            }
            updateSent(msgList);
        }
    }

    @Transactional
    private void updateSent(List<Notification> msgList) {
        msgList.forEach(msg -> msg.setSent(true));
        notificationRepository.saveAll(msgList);
    }

    @Transactional
    private List<Notification> saveMissedScheduleNotification(Schedule schedule) {
        Long ProtectedUserId = schedule.getProtectedUserId();
        User protectedUser = userRepository.findById(ProtectedUserId)
                .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));

        List<Long> caregivers = careRelationRepository
                .findByProtectedUserId(ProtectedUserId).stream()
                .map(CareRelation::getCaregiver)
                .map(User::getId)
                .toList();
        if (caregivers.isEmpty()) {
            throw new RuntimeException("해당 사용자의 보호자가 존재하지 않습니다.");
        }

        List<String> caregiverTokens = caregivers.stream()
                .map(caregiverId -> fcmTokenRepository.findByUserId(caregiverId)
                        .orElseThrow(() -> new RuntimeException("해당 사용자의 토큰이 존재하지 않습니다.")))
                .map(FcmToken::getToken)
                .toList();

        Notification msg = Notification.builder()
                .role(UserRole.보호자)
                .protectedUserId(ProtectedUserId)
                .scheduleId(schedule.getId())
                .title(schedule.getTitle())
                .type(schedule.getType())
                .notifiedAt(schedule.getDateTime())
                .sent(false)
                .build();

        String msgTitle = msg.getTitle() + " 미복용 알림";

        int hour = msg.getNotifiedAt().getHour();
        int minute = msg.getNotifiedAt().getMinute();
        String day = (hour < 12) ? "오전" : "오후";
        if (hour == 0) hour = 12;
        if (hour > 12) hour -= 12;

        String body = String.format("%s님이 오늘 %s %02d시 %02d분에 %s(을/를) 복용하지 않으셨습니다.",
                protectedUser.getName(), day, hour, minute, msg.getTitle());
        log.info("title: {}, body: {}", msgTitle, body);

        List<Notification> msgList = new ArrayList<>();
        for (int i = 0; i < caregivers.size(); i++) {
            msgList.add(buildNotification(
                    msg, caregivers.get(i), caregiverTokens.get(i), msgTitle, body));
        }

        return notificationRepository.saveAll(msgList);
    }

    private Notification buildNotification(
            Notification msg, Long caregiverId, String token, String msgTitle, String body) {
        msg = msg.toBuilder()
                .caregiverId(caregiverId)
                .token(token)
                .msgTitle(msgTitle)
                .body(body)
                .build();
        return msg;
    }



    private void sendNotification(Notification msg) throws FirebaseMessagingException {
        log.info(msg.toString());
        Message message = Message.builder()
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(msg.getMsgTitle())
                        .setBody(msg.getBody())
                        .build())
                .setToken(msg.getToken())
                .build();

        String response = FirebaseMessaging.getInstance().send(message);
        log.info("Successfully sent message: {}", response);

        updateSent(msg);
    }

    @Transactional
    private void updateSent(Notification msg) {
        msg.setSent(true);
        notificationRepository.save(msg);
    }

    private void sendNotifications(
            List<String> registrationTokens, String title, String body) throws FirebaseMessagingException {
        if (registrationTokens.isEmpty()) {
            log.info("No tokens");
            return;
        }

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .addAllTokens(registrationTokens)
                .build();

        BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);

        if (response.getFailureCount() > 0) {
            List<SendResponse> responses = response.getResponses();
            List<String> failedTokens = new ArrayList<>();
            for (int i = 0; i < responses.size(); i++) {
                if (!responses.get(i).isSuccessful()) {
                    failedTokens.add(registrationTokens.get(i));
                }
            }
            log.info("Failed to send messages: {}", failedTokens);
        }
    }

    private void sendNotifications(Notification msg) throws FirebaseMessagingException {
        List<String> registrationTokens = getCaregiverTokens(msg.getProtectedUserId());

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(msg.getMsgTitle())
                        .setBody(msg.getBody())
                        .build())
                .addAllTokens(registrationTokens)
                .build();

        BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);

        if (response.getFailureCount() > 0) {
            List<SendResponse> responses = response.getResponses();
            List<String> failedTokens = new ArrayList<>();
            for (int i = 0; i < responses.size(); i++) {
                if (!responses.get(i).isSuccessful()) {
                    failedTokens.add(registrationTokens.get(i));
                }
            }
            log.info("Failed to send messages: {}.", failedTokens);
        }
    }
}