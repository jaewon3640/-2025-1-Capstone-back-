package com.example.demo.service;

import com.example.demo.UserRole;
import com.example.demo.domain.CareRelation;
import com.example.demo.domain.FcmToken;
import com.example.demo.domain.Notification;
import com.example.demo.domain.User;
import com.example.demo.domain.schedule.Schedule;
import com.example.demo.exception.FcmTokenNotFoundException;
import com.example.demo.exception.ScheduleNotFoundException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.repository.*;
import com.google.firebase.messaging.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CaregiverNotificationService {

    private final NotificationRepository notificationRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;
    private final CareRelationRepository careRelationRepository;
    private final ScheduleRepository scheduleRepository;
    private final NotificationSendService notificationSendService;
    private final TaskScheduler taskScheduler;


    // 보호자 알림

    // 1. 오늘의 기분 확인 알림: 피보호자가 오늘의 기분 입력하면 전송(해당 보호자 기기)
    public void notifyHealthStatusCompleted(Long protectedUserId) throws FirebaseMessagingException {
        User protectedUser = userRepository.findById(protectedUserId)
                .orElseThrow(() -> new UserNotFoundException("해당 사용자가 존재하지 않습니다."));

        List<String> caregiverTokens = getCaregiverTokens(protectedUserId);
        String body = protectedUser.getName() + "님이 오늘의 기분을 입력하셨습니다.";
        notificationSendService.sendHealthStatusCompletedNotifications(caregiverTokens, "오늘의 기분 확인 알림", body);
    }

    private List<String> getCaregiverTokens(Long protectedUserId) {
        List<Long> caregivers = careRelationRepository
                .findByProtectedUserId(protectedUserId).stream()
                .map(CareRelation::getCaregiver)
                .map(User::getId)
                .toList();
        if (caregivers.isEmpty()) {
            throw new UserNotFoundException("해당 사용자의 보호자가 존재하지 않습니다.");
        }

        List<String> caregiverTokens = caregivers.stream()
                .map(caregiverId -> fcmTokenRepository.findByUserId(caregiverId)
                        .orElseThrow(() -> new FcmTokenNotFoundException(
                                "해당 보호자(ID: " + caregiverId + ")의 토큰이 존재하지 않습니다.")))
                .map(FcmToken::getToken)
                .toList();
        if (caregiverTokens.isEmpty()) {
            throw new FcmTokenNotFoundException("보호자의 토큰이 존재하지 않습니다.");
        }

        return caregiverTokens;
    }

    // 2. 약 복용 일정 미완료 알림: 설정된 시간 10분 후에 전송(해당 보호자 기기)
    @Transactional
    public void saveMissedScheduleNotifications(Schedule schedule) {
        Long ProtectedUserId = schedule.getProtectedUserId();
        User protectedUser = userRepository.findById(ProtectedUserId)
                .orElseThrow(() -> new UserNotFoundException("해당 사용자가 존재하지 않습니다."));

        List<Long> caregivers = careRelationRepository
                .findByProtectedUserId(schedule.getProtectedUserId()).stream()
                .map(CareRelation::getCaregiver)
                .map(User::getId)
                .toList();
        if (caregivers.isEmpty()) {
            throw new UserNotFoundException("해당 사용자의 보호자가 존재하지 않습니다.");
        }

        List<String> caregiverTokens = caregivers.stream()
                .map(caregiverId -> fcmTokenRepository.findByUserId(caregiverId)
                        .orElseThrow(() -> new FcmTokenNotFoundException(
                                "해당 보호자(ID: " + caregiverId + ")의 토큰이 존재하지 않습니다.")))
                .map(FcmToken::getToken)
                .toList();
        if (caregiverTokens.isEmpty()) {
            throw new FcmTokenNotFoundException("보호자의 토큰이 존재하지 않습니다.");
        }

        LocalDateTime dateTime = schedule.getDateTime().plusMinutes(10);

        Notification msg = Notification.builder()
                .role(UserRole.보호자)
                .protectedUserId(ProtectedUserId)
                .scheduleId(schedule.getId())
                .title(schedule.getTitle())
                .type(schedule.getType())
                .notifiedAt(dateTime)
                .sent(false)
                .build();

        String msgTitle = schedule.getTitle() + " 미복용 알림";

        int hour = schedule.getDateTime().getHour();
        int minute = schedule.getDateTime().getMinute();
        String day = (hour < 12) ? "오전" : "오후";
        if (hour == 0) hour = 12;
        if (hour > 12) hour -= 12;

        String body = String.format("%s님이 오늘 %s %02d시 %02d분에 %s을(를) 복용하지 않으셨습니다.",
                protectedUser.getName(), day, hour, minute, schedule.getTitle());

        List<Notification> msgList = new ArrayList<>();
        for (int i = 0; i < caregivers.size(); i++) {
            msgList.add(buildNotification(
                    msg, caregivers.get(i), caregiverTokens.get(i), msgTitle, body));
        }

        notificationRepository.saveAll(msgList);
        setMissedScheduleNotification(msgList);
    }

    private Notification buildNotification(
            Notification msg, Long caregiverId, String caregiverToken, String msgTitle, String body) {
        msg = msg.toBuilder()
                .caregiverId(caregiverId)
                .token(caregiverToken)
                .msgTitle(msgTitle)
                .body(body)
                .build();
        return msg;
    }

    public void setMissedScheduleNotification(List<Notification> msgList) {
        LocalDateTime dateTime = msgList.get(0).getNotifiedAt();
        taskScheduler.schedule(() -> notifyMissedSchedule(msgList),
                dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public void notifyMissedSchedule(List<Notification> msgList) {
        Notification msg = msgList.get(0);
        if (!checkCompleted(msg.getScheduleId())) {
            List<String> caregiverTokens = getCaregiverTokens(msg.getProtectedUserId());
            try {
                notificationSendService.sendMissedScheduleNotifications(caregiverTokens, msgList);
            } catch (FirebaseMessagingException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            deleteMissedScheduleNotification(msgList);
        }
    }

    @Transactional
    private void deleteMissedScheduleNotification(List<Notification> msgList) {
        notificationRepository.deleteAll(msgList);
    }

    private boolean checkCompleted(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("해당 일정이 존재하지 않습니다."))
                .isCompleted();
    }

    public void notifyMissedSchedule(Notification msg) {
        if (!checkCompleted(msg.getScheduleId())) {
            try {
                notificationSendService.sendNotification(msg);
            } catch (FirebaseMessagingException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            deleteMissedScheduleNotification(msg);
        }
    }

    @Transactional
    private void deleteMissedScheduleNotification(Notification msg) {
        notificationRepository.delete(msg);
    }
}