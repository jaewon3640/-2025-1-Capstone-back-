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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public FcmTokenResponse saveFcmToken(FcmTokenRequest request) {
        Long userId = request.getUserId();
        if (fcmTokenRepository.existsByUserId(userId)) {
           FcmToken fcmToken = fcmTokenRepository.findByUserId(userId)
                    .orElseThrow(() -> new FcmTokenNotFoundException("해당 사용자의 토큰이 존재하지 않습니다."));
            log.info("current token: {}", fcmToken.getToken());
            fcmToken.setToken(request.getToken());
            log.info("changed token: {}", fcmToken.getToken());
            notificationService.updateNotificationsByUserId(fcmToken);
            return new FcmTokenResponse(fcmTokenRepository.save(fcmToken));
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("해당 사용자가 존재하지 않습니다."));
        FcmToken newToken = FcmToken.builder()
                .userId(userId)
                .role(user.getRole())
                .token(request.getToken())
                .build();
        log.info(newToken.toString());
        return new FcmTokenResponse(fcmTokenRepository.save(newToken));
    }

    @Transactional
    public FcmTokenResponse deleteFcmToken(Long id) {
        FcmToken token = fcmTokenRepository.findById(id)
                .orElseThrow(() -> new FcmTokenNotFoundException("해당 토큰이 존재하지 않습니다."));
        log.info(token.toString());
        fcmTokenRepository.delete(token);
        return new FcmTokenResponse(token);
    }
}