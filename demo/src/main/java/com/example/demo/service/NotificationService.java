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
import com.google.firebase.messaging.FirebaseMessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

@RequiredArgsConstructor
@Service
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;
    private final ProtectedUserNotificationService protectedUserNotificationService;
    private final CaregiverNotificationService caregiverNotificationService;
    private final NotificationSendService notificationSendService;
    private final TaskScheduler taskScheduler;
    private final ScheduledTaskService scheduledTaskService;

    @PostConstruct
    public void init() {
        List<Notification> msgList = notificationRepository
                .findBySentAndNotifiedAtAfter(false, LocalDateTime.now());
        if (!msgList.isEmpty()) {
            msgList.forEach(msg -> {
                LocalDateTime dateTime = msg.getNotifiedAt();
                ScheduledFuture<?> future;
                if (msg.getRole() == UserRole.보호자) {
                    future = taskScheduler.schedule(
                            () -> caregiverNotificationService.notifyMissedSchedule(msg),
                            dateTime.atZone(ZoneId.systemDefault()).toInstant());
                    scheduledTaskService.registerTask(msg.getId(), future);
                }
                else {
                    future = taskScheduler.schedule(() -> {
                        try {
                            notificationSendService.sendNotification(msg);
                        } catch (FirebaseMessagingException e) {
                            throw new RuntimeException(e);
                        }
                    }, dateTime.atZone(ZoneId.systemDefault()).toInstant());
                    scheduledTaskService.registerTask(msg.getId(), future);
                }
            });
        }
    }

    public void saveRecurringNotifications(List<Schedule> schedules) {
        schedules.forEach(schedule -> {
            if (schedule.getDateTime().isAfter(LocalDateTime.now())) {
                protectedUserNotificationService.saveUpcomingScheduleNotification(schedule);
                if (schedule.getType() == ScheduleType.MEDICINE) {
                    protectedUserNotificationService.saveScheduleCompletedCheckNotification(schedule);
                    caregiverNotificationService.saveMissedScheduleNotifications(schedule);
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
            caregiverNotificationService.saveMissedScheduleNotifications(schedule);
        }
    }

    private boolean isDuplicateNotification(Schedule schedule) {
        return notificationRepository.existsByScheduleIdAndTitleAndTypeAndNotifiedAt(
                schedule.getId(), schedule.getTitle(),
                schedule.getType(), schedule.getDateTime());
    }

    @Transactional
    public void updateNotificationTokens(FcmToken fcmToken) {
        List<Notification> msgList;
        if (fcmToken.getRole() == UserRole.보호자) {
            msgList = notificationRepository.findByCaregiverId(fcmToken.getUserId());
        }
        else {
            msgList = notificationRepository
                    .findByProtectedUserIdAndRole(fcmToken.getUserId(), UserRole.피보호자);
        }

        if (!msgList.isEmpty()) {
            msgList.forEach(msg -> msg.setToken(fcmToken.getToken()));
            notificationRepository.saveAll(msgList);
        }
    }

    public List<NotificationResponse> readUserNotifications(Long userId) {
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
        List<Notification> msgList = notificationRepository.findByScheduleId(schedule.getId());
        if (!msgList.isEmpty()) {
            msgList.forEach(msg -> setNotification(msg, schedule));
            notificationRepository.saveAll(msgList);
        }
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

        List<Long> notificationIdList = scheduleIdList.stream()
                .flatMap(scheduleId -> notificationRepository.findByScheduleId(scheduleId).stream())
                .map(Notification::getId)
                .toList();

        notificationIdList.forEach(scheduledTaskService::cancelTask);
        scheduleIdList.forEach(notificationRepository::deleteByScheduleId);
    }
    




    @Transactional
    public NotificationResponse createNotification(NotificationRequest request) {
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
        Notification msg = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("해당 알림이 존재하지 않습니다."));
        setNotification(msg, request);
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

        String title = (msg.getType() == ScheduleType.MEDICINE)
                ? msg.getTitle() + " 복용 알림" : msg.getTitle() + " 방문 알림";
        msg.setMsgTitle(title);

        int hour = msg.getNotifiedAt().getHour();
        int minute = msg.getNotifiedAt().getMinute();
        String day = (hour < 12) ? "오전" : "오후";
        if (hour == 0) hour = 12;
        if (hour > 12) hour -= 12;

        String body = String.format("오늘 %s %02d시 %02d분에 %s%s", day, hour, minute, msg.getTitle(),
                (msg.getType() == ScheduleType.MEDICINE) ? "을(를) 복용하셔야 합니다." : " 방문 일정이 있습니다.");
        msg.setBody(body);

        notificationSendService.sendNotification(msg);
        return new NotificationResponse(msg);
    }
}