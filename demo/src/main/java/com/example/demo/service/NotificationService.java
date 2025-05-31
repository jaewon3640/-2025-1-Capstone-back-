package com.example.demo.service;

import com.example.demo.DTO.NotificationRequest;
import com.example.demo.DTO.NotificationResponse;
import com.example.demo.UserRole;
import com.example.demo.domain.FcmToken;
import com.example.demo.domain.Notification;
import com.example.demo.domain.User;
import com.example.demo.domain.schedule.Schedule;
import com.example.demo.domain.schedule.ScheduleType;
import com.example.demo.exception.FcmTokenNotFoundException;
import com.example.demo.exception.NotificationNotFoundException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.repository.FcmTokenRepository;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.UserRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;
    private final ProtectedUserNotificationService protectedUserNotificationService;
    private final CaregiverNotificationService caregiverNotificationService;

    public void saveRecurringNotifications(List<Schedule> schedules) {
        schedules.forEach(schedule -> {
            if (schedule.getDateTime().isAfter(LocalDateTime.now())) {
                protectedUserNotificationService.saveUpcomingScheduleNotification(schedule);
                if (schedule.getType() == ScheduleType.MEDICINE) {
                    protectedUserNotificationService.saveScheduleCompletedCheckNotification(schedule);
                    caregiverNotificationService.setMissedScheduleNotification(schedule);
                }
            }
        });
    }

    public void saveNonRecurringNotification(Schedule schedule) {
        if (!userRepository.existsById(schedule.getProtectedUserId())) {
            throw new UserNotFoundException("해당 사용자가 존재하지 않습니다.");
        }
        if (isDuplicateNotification(schedule)) {
            throw new RuntimeException("이미 동일한 알림이 설정되어 있습니다.");
        }
        if (!schedule.getDateTime().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("과거 시간으로 알림을 설정할 수 없습니다.");
        }

        protectedUserNotificationService.saveUpcomingScheduleNotification(schedule);
        if (schedule.getType() == ScheduleType.MEDICINE) {
            protectedUserNotificationService.saveScheduleCompletedCheckNotification(schedule);
            caregiverNotificationService.setMissedScheduleNotification(schedule);
        }
    }

    private boolean isDuplicateNotification(Schedule schedule) {
        return notificationRepository.existsByScheduleIdAndTitleAndTypeAndNotifiedAt(
                schedule.getId(), schedule.getTitle(),
                schedule.getType(), schedule.getDateTime());
    }

    @Transactional
    public void updateNotificationsByUserId(FcmToken fcmToken) {
        log.info(fcmToken.toString());
        List<Notification> msgList;
        if (fcmToken.getRole() == UserRole.보호자) {
            msgList = notificationRepository.findByCaregiverId(fcmToken.getUserId());
        }
        else {
            msgList = notificationRepository
                    .findByProtectedUserIdAndRole(fcmToken.getUserId(), UserRole.피보호자);
        }
        if (msgList.isEmpty()) {
            throw new NotificationNotFoundException("알림이 존재하지 않습니다.");
        }
        msgList.forEach(msg -> msg.setToken(fcmToken.getToken()));
        notificationRepository.saveAll(msgList);
    }

    public List<NotificationResponse> readUserNotifications(Long userId) {
        log.info("userId = {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("해당 사용자가 존재하지 않습니다."));

        List<NotificationResponse> msgList;
        if (user.getRole() == UserRole.보호자) {
            msgList = notificationRepository.findByCaregiverId(userId).stream()
                    .map(NotificationResponse::new)
                    .toList();
        }
        else {
            msgList = notificationRepository.findByProtectedUserId(userId).stream()
                    .map(NotificationResponse::new)
                    .toList();
        }
        if (msgList.isEmpty()) {
            throw new NotificationNotFoundException("알림이 존재하지 않습니다.");
        }
        return msgList;
    }

    public NotificationResponse readNotification(Long id) {
        Notification msg = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("해당 알림이 존재하지 않습니다."));
        return new NotificationResponse(msg);
    }

    @Transactional
    public void updateNotifications(Schedule schedule) {
        log.info(schedule.toString());
        List<Notification> msgList = notificationRepository.findByScheduleId(schedule.getId());
        if (msgList.isEmpty()) {
            throw new NotificationNotFoundException("알림이 존재하지 않습니다.");
        }
        log.info("current message: {}", msgList.get(0));
        msgList.forEach(msg -> setNotification(msg, schedule));
        log.info("changed message: {}", msgList.get(0));
        notificationRepository.saveAll(msgList);
    }

    private void setNotification(Notification msg, Schedule schedule) {
        msg.setTitle(schedule.getTitle());
        msg.setType(schedule.getType());
        if (msg.getType() != schedule.getType()) {
            msg.setBody(null);
        }
        if (schedule.getDateTime().isAfter(LocalDateTime.now())) {
            msg.setNotifiedAt(schedule.getDateTime());
        } else {
            throw new RuntimeException("과거 시간으로 알림을 설정할 수 없습니다.");
        }
        if (msg.isSent() && schedule.getDateTime().isAfter(LocalDateTime.now())) {
            msg.setSent(false);
        }
    }

    @Transactional
    public void deleteNotifications(List<Schedule> schedules) {
        List<Long> scheduleIdList = schedules.stream()
                .map(Schedule::getId)
                .distinct()
                .toList();
        scheduleIdList.forEach(notificationRepository::deleteByScheduleId);
    }
    


    @Transactional
    public NotificationResponse createNotification(NotificationRequest request) {
        log.info(request.toString());
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("해당 사용자가 존재하지 않습니다."));
        UserRole role = user.getRole();
        if (isDuplicateNotification(request, role)) {
            throw new RuntimeException("이미 동일한 알림이 설정되어 있습니다.");
        }
        String userToken = fcmTokenRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new FcmTokenNotFoundException("해당 사용자의 토큰이 존재하지 않습니다."))
                .getToken();

        Notification msg = Notification.builder()
                .token(userToken)
                .title(request.getTitle())
                .type(request.getType())
                .notifiedAt(request.getNotifiedAt())
                .build();

        if (role == UserRole.보호자) {
            msg = msg.toBuilder()
                    .role(UserRole.보호자)
                    .caregiverId(request.getUserId())
                    .build();
        }
        else {
            msg = msg.toBuilder()
                    .role(UserRole.피보호자)
                    .protectedUserId(request.getUserId())
                    .build();
        }

        return new NotificationResponse(notificationRepository.save(msg));
    }

    private boolean isDuplicateNotification(NotificationRequest request, UserRole role) {
        if (role == UserRole.보호자) {
            return notificationRepository.existsByCaregiverIdAndTitleAndTypeAndNotifiedAt(
                    request.getUserId(), request.getTitle(),
                    request.getType(), request.getNotifiedAt());
        }
        else {
            return notificationRepository.existsByProtectedUserIdAndTitleAndTypeAndNotifiedAt(
                    request.getUserId(), request.getTitle(),
                    request.getType(), request.getNotifiedAt());
        }
    }

    @Transactional
    public NotificationResponse updateNotification(Long id, NotificationRequest request) {
        log.info(request.toString());
        Notification msg = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("해당 알림이 존재하지 않습니다."));
        log.info("current msg: {}", msg);
        setNotification(msg, request);
        log.info("changed msg: {}", msg);
        return new NotificationResponse(notificationRepository.save(msg));
    }

    private void setNotification(Notification msg, NotificationRequest request) {
        msg.setTitle(request.getTitle());
        msg.setType(request.getType());
        if (msg.getType() != request.getType()) {
            msg.setBody(null);
        }
        if (request.getNotifiedAt().isAfter(LocalDateTime.now())) {
            msg.setNotifiedAt(request.getNotifiedAt());
        } else {
            throw new RuntimeException("과거 시간으로 알림을 설정할 수 없습니다.");
        }
        if (msg.isSent() && request.getNotifiedAt().isAfter(LocalDateTime.now())) {
            msg.setSent(false);
        }
    }

    @Transactional
    public NotificationResponse deleteNotificationById(Long id) {
        Notification msg = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("해당 알림이 존재하지 않습니다."));
        notificationRepository.delete(msg);
        return new NotificationResponse(msg);
    }

    public NotificationResponse sendNotification(Long id) throws FirebaseMessagingException {
        Notification msg = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("해당 알림이 존재하지 않습니다."));
        log.info(msg.toString());
        String registrationToken = msg.getToken();
        String title = (msg.getType() == ScheduleType.MEDICINE)
                ? msg.getTitle() + " 복용 알림" : msg.getTitle() + " 방문 알림";

        int hour = msg.getNotifiedAt().getHour();
        int minute = msg.getNotifiedAt().getMinute();
        String day = (hour < 12) ? "오전" : "오후";
        if (hour == 0) hour = 12;
        if (hour > 12) hour -= 12;

        String body = String.format("오늘 %s %02d시 %02d분에 %s%s", day, hour, minute, msg.getTitle(),
                (msg.getType() == ScheduleType.MEDICINE) ? "(을/를) 복용하셔야 합니다." : " 방문 일정이 있습니다.");
        log.info("title: {}, body: {}", title, body);

        Message message = Message.builder()
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .setToken(registrationToken)
                .build();

        String response = FirebaseMessaging.getInstance().send(message);
        log.info("Successfully sent message: {}", response);
        return new NotificationResponse(msg);
    }
}