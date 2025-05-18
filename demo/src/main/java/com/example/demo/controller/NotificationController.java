package com.example.demo.controller;

import com.example.demo.DTO.NotificationRequest;
import com.example.demo.DTO.NotificationResponse;
import com.example.demo.service.NotificationService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/notifications")
@RestController
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(@RequestBody NotificationRequest request) {
        log.info("(NotificationController) create notification");
        return ResponseEntity.ok(notificationService.createNotification(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> readNotifications(@PathVariable("userId") String userId) {
        log.info("(NotificationController) read notifications");
        return ResponseEntity.ok(notificationService.readNotifications(userId));
    }

    @GetMapping("/{numId}")
    public ResponseEntity<NotificationResponse> readNotification(@PathVariable("numId") Long numId) {
        log.info("(NotificationController) read notification");
        return ResponseEntity.ok(notificationService.readNotification(numId));
    }

    @PutMapping("/{numId}")
    public ResponseEntity<NotificationResponse> updateNotification(@PathVariable Long numId, @RequestBody NotificationRequest request) {
        log.info("(NotificationController) update notification");
        return ResponseEntity.ok(notificationService.updateNotification(numId, request));
    }

    @DeleteMapping("/{numId}")
    public ResponseEntity<NotificationResponse> deleteNotification(@PathVariable Long numId) {
        log.info("(NotificationController) delete notification");
        return ResponseEntity.ok(notificationService.deleteNotification(numId));
    }

    @PostMapping("/send/{numId}")
    public ResponseEntity<NotificationResponse> sendNotification(@PathVariable Long numId) throws FirebaseMessagingException {
        log.info("(NotificationController) send notification");
        log.info("numId = " + numId);
        return ResponseEntity.ok(notificationService.sendNotification(numId));
    }
}