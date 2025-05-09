package com.example.demo.controller;

import com.example.demo.DTO.FcmTokenRequest;
import com.example.demo.DTO.FcmTokenResponse;
import com.example.demo.service.FcmTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class FcmTokenController {
    private final FcmTokenService fcmTokenService;

    @PostMapping("/api/fcm/token")
    public ResponseEntity<FcmTokenResponse> saveFcmToken(@RequestBody FcmTokenRequest request) {
        log.info("(FcmTokenController) save FcmToken");
        return ResponseEntity.ok(fcmTokenService.saveFcmToken(request));
    }
}