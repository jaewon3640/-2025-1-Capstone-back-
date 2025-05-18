package com.example.demo.service;

import com.example.demo.DTO.NotificationRequest;
import com.example.demo.DTO.NotificationResponse;
import com.example.demo.domain.FcmToken;
import com.example.demo.domain.Notification;
import com.example.demo.repository.FcmTokenRepository;
import com.example.demo.repository.NotificationRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final FcmTokenRepository fcmTokenRepository;

    @Transactional
    public NotificationResponse createNotification(NotificationRequest request) {
        FcmToken token = fcmTokenRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("토큰이 존재하지 않습니다."));
        Notification msg = Notification.builder()
                .userId(request.getUserId())
                .token(token.getToken())
                .title(request.getTitle())
                .body(request.getBody())
                .build();
        log.info(msg.toString());
        return new NotificationResponse(notificationRepository.save(msg));
    }

    public List<NotificationResponse> readNotifications(String userId) {
        log.info("userId = " + userId);
        return notificationRepository.findByUserId(userId).stream()
                .map(NotificationResponse::new)
                .collect(Collectors.toList());
    }

    public NotificationResponse readNotification(Long numId) {
        log.info("numId = " + numId);
        Notification msg = notificationRepository.findByNumId(numId)
                .orElseThrow(() -> new RuntimeException("해당 알림이 존재하지 않습니다."));
        return new NotificationResponse(msg);
    }

    @Transactional
    public NotificationResponse updateNotification(Long numId, NotificationRequest request) {
        Notification msg = notificationRepository.findByNumId(numId)
                .orElseThrow(() -> new RuntimeException("해당 알림이 존재하지 않습니다."));
        log.info(msg.toString());
        msg.setTitle(request.getTitle());
        msg.setBody(request.getBody());
        return new NotificationResponse(notificationRepository.save(msg));
    }

    @Transactional
    public NotificationResponse deleteNotification(Long numId) {
        Notification msg = notificationRepository.findByNumId(numId)
                .orElseThrow(() -> new RuntimeException("해당 알림이 존재하지 않습니다."));
        log.info(msg.toString());
        notificationRepository.delete(msg);
        return new NotificationResponse(msg);
    }

    public NotificationResponse sendNotification(Long numId) throws FirebaseMessagingException {
        Notification msg = notificationRepository.findByNumId(numId)
                .orElseThrow(() -> new RuntimeException("해당 알림이 존재하지 않습니다."));
        log.info(msg.toString());
        String registrationToken = msg.getToken();

        Message message = Message.builder()
                .setNotification(com.google.firebase.messaging.Notification.builder()
                .setTitle(msg.getTitle())
                .setBody(msg.getBody())
                .build())
                .setToken(registrationToken)
                .build();

        String response = FirebaseMessaging.getInstance().send(message);
        log.info("Successfully sent message: " + message);
        return new NotificationResponse(msg);
    }
}