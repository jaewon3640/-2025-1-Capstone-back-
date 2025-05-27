package com.example.demo.service;

import com.example.demo.DTO.FcmTokenRequest;
import com.example.demo.DTO.FcmTokenResponse;
import com.example.demo.domain.FcmToken;
import com.example.demo.domain.User;
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

    @Transactional
    public FcmTokenResponse saveFcmToken(FcmTokenRequest request) {
        if (fcmTokenRepository.existsByUserIdAndToken(request.getUserId(), request.getToken())) {
            throw new RuntimeException("이미 존재하는 토큰입니다.");
        }
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));
        FcmToken newToken = FcmToken.builder()
                .userId(request.getUserId())
                .role(user.getRole())
                .token(request.getToken())
                .build();
        log.info(newToken.toString());
        return new FcmTokenResponse(fcmTokenRepository.save(newToken));
    }

    @Transactional
    public FcmTokenResponse deleteFcmToken(Long id) {
        FcmToken token = fcmTokenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 토큰이 존재하지 않습니다."));
        log.info(token.toString());
        fcmTokenRepository.delete(token);
        return new FcmTokenResponse(token);
    }
}