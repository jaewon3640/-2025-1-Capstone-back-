package com.example.demo.controller;

import com.example.demo.DTO.NotificationRequest;
import com.example.demo.DTO.NotificationResponse;
import com.example.demo.service.NotificationService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/notifications")
@RestController
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> readAllNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.readAllNotifications(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> readNotification(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.readNotification(id));
    }


    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(@RequestBody NotificationRequest request) throws FirebaseMessagingException {
        return ResponseEntity.ok(notificationService.createNotification(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationResponse> updateNotification(@PathVariable Long id, @RequestBody NotificationRequest request) {
        return ResponseEntity.ok(notificationService.updateNotification(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<NotificationResponse> deleteNotification(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.deleteNotificationById(id));
    }

    @PostMapping("/send/{id}")
    public ResponseEntity<NotificationResponse> sendNotification(@PathVariable Long id) throws FirebaseMessagingException {
        return ResponseEntity.ok(notificationService.sendNotification(id));
    }
}