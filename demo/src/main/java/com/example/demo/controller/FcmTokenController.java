package com.example.demo.controller;

import com.example.demo.DTO.FcmTokenRequest;
import com.example.demo.DTO.FcmTokenResponse;
import com.example.demo.service.FcmTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/fcm/tokens")
@RestController
public class FcmTokenController {

    private final FcmTokenService fcmTokenService;

    @PostMapping
    public ResponseEntity<FcmTokenResponse> saveFcmToken(@RequestBody FcmTokenRequest request) {
        return ResponseEntity.ok(fcmTokenService.saveFcmToken(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FcmTokenResponse> deleteFcmToken(@PathVariable Long id) {
        return ResponseEntity.ok(fcmTokenService.deleteFcmToken(id));
    }
}