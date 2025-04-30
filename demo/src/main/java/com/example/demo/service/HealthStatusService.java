package com.example.demo.service;


import com.example.demo.DTO.HealthStatusRequest;
import com.example.demo.DTO.HealthStatusResponse;
import com.example.demo.domain.HealthStatus;
import com.example.demo.domain.User;
import com.example.demo.repository.HealthStatusRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HealthStatusService {

    private final HealthStatusRepository healthStatusRepository;
    private final UserRepository userRepository;

    public HealthStatusResponse saveFromDto(HealthStatusRequest request) {
        User protectedUser = userRepository.findById(request.getProtectedUserId())
                .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));
        HealthStatus status = HealthStatus.builder()
                .protectedUser(protectedUser)
                .type(request.getType())
                .measurement(request.getMeasurement())
                .recordedAt(request.getRecordedAt())
                .build();

        return new HealthStatusResponse(healthStatusRepository.save(status));
    }

    public List<HealthStatusResponse> getByUserId(Long protectedUserId) {
        return healthStatusRepository.findByProtectedUserId(protectedUserId).stream()
                .map(HealthStatusResponse::new)
                .collect(Collectors.toList());
    }
}