package com.example.demo.service;

import com.example.demo.domain.Notification;
import com.example.demo.repository.*;
import com.google.firebase.messaging.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationSendService {

    private final NotificationRepository notificationRepository;

    @Transactional
    private void updateSent(Notification msg) {
        msg.setSent(true);
        notificationRepository.save(msg);
    }

    @Transactional
    private void updateSent(List<Notification> msgList) {
        msgList.forEach(msg -> msg.setSent(true));
        notificationRepository.saveAll(msgList);
    }

    private void checkFailure(BatchResponse response, List<String> registrationTokens) {
        List<SendResponse> responses = response.getResponses();
        List<String> failedTokens = new ArrayList<>();
        for (int i = 0; i < responses.size(); i++) {
            if (!responses.get(i).isSuccessful()) {
                failedTokens.add(registrationTokens.get(i));
            }
        }
        log.info("Failed to send messages: {}", failedTokens);
    }

    // 오늘의 기분 입력 알림(피보호자)
    public void sendHealthStatusCheckNotifications(
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
                .putData("type", "EMOTION_CHECK")
                .addAllTokens(registrationTokens)
                .build();

        BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);

        if (response.getFailureCount() > 0) {
            checkFailure(response, registrationTokens);
        }
    }

    // 일정 시작 전 알림(피보호자)
    public void sendNotification(Notification msg) throws FirebaseMessagingException {
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

    // 약 복용 일정 완료 확인 알림(피보호자)
    public void sendScheduleCompletedCheckNotification(
            Notification msg, String time) throws FirebaseMessagingException {

        Message message = Message.builder()
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(msg.getMsgTitle())
                        .setBody(msg.getBody())
                        .build())
                .putData("type", "TODAY_CHECK")
                .putData("scheduleId", msg.getScheduleId().toString())
                .putData("time", time)
                .putData("message", msg.getBody())
                .setToken(msg.getToken())
                .build();

        String response = FirebaseMessaging.getInstance().send(message);
        log.info("Successfully sent message: {}.", response);

        updateSent(msg);
    }

    // 오늘의 기분 확인 알림(보호자)
    public void sendHealthStatusCompletedNotifications(
            List<String> registrationTokens, String title, String body) throws FirebaseMessagingException {

        if (registrationTokens.isEmpty()) {
            log.info("No tokens.");
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
            checkFailure(response, registrationTokens);
        }
    }

    // 약 복용 일정 미완료 알림(보호자)
    public void sendMissedScheduleNotifications(
            List<String> registrationTokens, List<Notification> msgList) throws FirebaseMessagingException {

        Notification msg = msgList.get(0);
        MulticastMessage message = MulticastMessage.builder()
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(msg.getMsgTitle())
                        .setBody(msg.getBody())
                        .build())
                .addAllTokens(registrationTokens)
                .build();

        BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);

        if (response.getFailureCount() > 0) {
            checkFailure(response, registrationTokens);
        }

        updateSent(msgList);
    }
}