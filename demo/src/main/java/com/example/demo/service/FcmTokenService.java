package com.example.demo.service;

import com.example.demo.DTO.FcmTokenRequest;
import com.example.demo.DTO.FcmTokenResponse;
import com.example.demo.domain.FcmToken;
import com.example.demo.repository.FcmTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class FcmTokenService {
    private final FcmTokenRepository fcmTokenRepository;

    @Transactional
    public FcmTokenResponse saveFcmToken(FcmTokenRequest request) {
        FcmToken token = FcmToken.builder()
                .userId(request.getUserId())
                .token(request.getToken())
                .build();
        log.info(token.toString());
        FcmToken userToken = fcmTokenRepository.save(token);
        log.info(userToken.toString());
        return new FcmTokenResponse(token);
    }
}