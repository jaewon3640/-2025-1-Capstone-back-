package com.example.demo.service;

import com.example.demo.DTO.FcmTokenRequest;
import com.example.demo.DTO.FcmTokenResponse;
import com.example.demo.domain.FcmToken;
import com.example.demo.domain.User;
import com.example.demo.exception.FcmTokenNotFoundException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.repository.FcmTokenRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public FcmTokenResponse saveFcmToken(FcmTokenRequest request) {
        Long userId = request.getUserId();
        String newToken = request.getToken();

        if (fcmTokenRepository.existsByUserId(userId)) {
           FcmToken fcmToken = fcmTokenRepository.findByUserId(userId)
                    .orElseThrow(() -> new FcmTokenNotFoundException("해당 사용자의 토큰이 존재하지 않습니다."));
           if (!Objects.equals(newToken, fcmToken.getToken())) {
               fcmToken.setToken(newToken);
               notificationService.updateNotificationTokens(fcmToken);
               return new FcmTokenResponse(fcmTokenRepository.save(fcmToken));
           }
           return new FcmTokenResponse(fcmToken);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("해당 사용자가 존재하지 않습니다."));
        FcmToken newFcmtoken = FcmToken.builder()
                .userId(userId)
                .role(user.getRole())
                .token(newToken)
                .build();
        return new FcmTokenResponse(fcmTokenRepository.save(newFcmtoken));
    }

    @Transactional
    public FcmTokenResponse deleteFcmToken(Long id) {
        FcmToken token = fcmTokenRepository.findById(id)
                .orElseThrow(() -> new FcmTokenNotFoundException("해당 토큰이 존재하지 않습니다."));
        fcmTokenRepository.delete(token);
        return new FcmTokenResponse(token);
    }
}