package com.example.demo.service;

import com.example.demo.UserRole;
import com.example.demo.domain.FcmToken;
import com.example.demo.domain.Notification;
import com.example.demo.domain.schedule.Schedule;
import com.example.demo.domain.schedule.ScheduleType;
import com.example.demo.exception.FcmTokenNotFoundException;
import com.example.demo.repository.*;
import com.google.firebase.messaging.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProtectedUserNotificationService {

    private final NotificationRepository notificationRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final NotificationSendService notificationSendService;
    private final TaskScheduler taskScheduler;


    // 피보호자 알림

    // 1. 오늘의 기분 입력 알림: 매일 오후 8시에 전송(모든 피보호자 기기)
    // 알림 유형(type), 피보호자 id(protectedUserId)도 전송
    @Scheduled(cron = "0 0 20 * * *")
    public void notifyHealthStatusCheck() throws FirebaseMessagingException {
        List<FcmToken> protectedUserList = fcmTokenRepository.findByRole(UserRole.피보호자);
        if (protectedUserList.isEmpty()) {
            throw new FcmTokenNotFoundException("피보호자가 존재하지 않습니다.");
        }

        List<String> protectedUserTokens = protectedUserList.stream()
                .map(FcmToken::getToken)
                .toList();
        List<Long> protectedUserIdList = protectedUserList.stream()
                .map(FcmToken::getUserId)
                .toList();

        for (int i = 0; i < protectedUserList.size(); i++) {
            notificationSendService.sendHealthStatusCheckNotifications(
                    protectedUserTokens.get(i), protectedUserIdList.get(i),
                    "오늘의 기분 입력 알림", "오늘의 기분을 입력해주세요.");
        }
    }

    // 2. 일정 시작 전 알림: 설정된 시간 10분(약 복용) / 1시간(병원) 전에 전송(해당 피보호자 기기)
    @Transactional
    public void saveUpcomingScheduleNotification(Schedule schedule) {
        String protectedUserToken = fcmTokenRepository.findByUserId(schedule.getProtectedUserId())
                .orElseThrow(() -> new FcmTokenNotFoundException("해당 사용자의 토큰이 존재하지 않습니다."))
                .getToken();

        LocalDateTime dateTime = (schedule.getType() == ScheduleType.MEDICINE)
                ? schedule.getDateTime().minusMinutes(10)
                : schedule.getDateTime().minusHours(1);

        Notification msg = Notification.builder()
                .role(UserRole.피보호자)
                .protectedUserId(schedule.getProtectedUserId())
                .token(protectedUserToken)
                .scheduleId(schedule.getId())
                .title(schedule.getTitle())
                .type(schedule.getType())
                .notifiedAt(dateTime)
                .sent(false)
                .build();

        String msgTitle = (schedule.getType() == ScheduleType.MEDICINE)
                ? schedule.getTitle() + " 복용 전 알림" : schedule.getTitle() + " 방문 전 알림";

        int hour = schedule.getDateTime().getHour();
        int minute = schedule.getDateTime().getMinute();
        String day = (hour < 12) ? "오전" : "오후";
        if (hour == 0) hour = 12;
        if (hour > 12) hour -= 12;

        String body = String.format("오늘 %s %02d시 %02d분에 %s%s", day, hour, minute, schedule.getTitle(),
                (schedule.getType() == ScheduleType.MEDICINE) ? "을(를) 복용하셔야 합니다." : " 방문 일정이 있습니다.");

        msg = msg.toBuilder()
                .msgTitle(msgTitle)
                .body(body)
                .build();

        notificationRepository.save(msg);
        setUpcomingScheduleNotification(msg);
    }

    public void setUpcomingScheduleNotification(Notification msg) {
        LocalDateTime dateTime = msg.getNotifiedAt();
        taskScheduler.schedule(() -> {
            try {
                notificationSendService.sendNotification(msg);
            } catch (FirebaseMessagingException e) {
                throw new RuntimeException(e);
            }
        }, dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    // 3. 약 복용 일정 완료 확인 알림: 설정된 시간에 전송(해당 피보호자 기기)
    // 알림 유형(type), scheduleId, 알림 내용(message), 일정 시간(time)도 전송
    @Transactional
    public void saveScheduleCompletedCheckNotification(Schedule schedule) {
        String protectedUserToken = fcmTokenRepository.findByUserId(schedule.getProtectedUserId())
                .orElseThrow(() -> new FcmTokenNotFoundException("해당 사용자의 토큰이 존재하지 않습니다."))
                .getToken();

        Notification msg = Notification.builder()
                .role(UserRole.피보호자)
                .protectedUserId(schedule.getProtectedUserId())
                .token(protectedUserToken)
                .scheduleId(schedule.getId())
                .title(schedule.getTitle())
                .type(schedule.getType())
                .notifiedAt(schedule.getDateTime())
                .sent(false)
                .build();

        String msgTitle = schedule.getTitle() + " 복용 알림";

        int hour = schedule.getDateTime().getHour();
        int minute = schedule.getDateTime().getMinute();
        String day = (hour < 12) ? "오전" : "오후";
        if (hour == 0) hour = 12;
        if (hour > 12) hour -= 12;

        String time = String.format("%s %02d : %02d", day, hour, minute);
        String body = String.format("오늘 %s을(를) 복용하실 시간입니다.", schedule.getTitle());

        msg = msg.toBuilder()
                .msgTitle(msgTitle)
                .body(body)
                .build();

        notificationRepository.save(msg);
        setScheduleCompletedCheckNotification(msg, time);
    }

    public void setScheduleCompletedCheckNotification(Notification msg, String time) {
        LocalDateTime dateTime = msg.getNotifiedAt();
        taskScheduler.schedule(() -> {
            try {
                notificationSendService.sendScheduleCompletedCheckNotification(msg, time);
            } catch (FirebaseMessagingException e) {
                throw new RuntimeException(e);
            }
        }, dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}