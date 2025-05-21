package com.example.demo.service;


import com.example.demo.DTO.HealthStatusRequest;
import com.example.demo.DTO.HealthStatusResponse;
import com.example.demo.MoodStatus;
import com.example.demo.domain.HealthStatus;
import com.example.demo.domain.User;
import com.example.demo.exception.UserNotFoundException;
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
                .orElseThrow(() -> new UserNotFoundException("해당 사용자가 존재하지 않습니다."));
        HealthStatus status = HealthStatus.builder()
                .protectedUser(protectedUser)
                .checkBreakfast(request.isCheckBreakfast())
                .checkMedicine(request.isCheckMedicine())
                .mood(request.getMood())
                .recordedAt(request.getRecordedAt())
                .build();

        return new HealthStatusResponse(healthStatusRepository.save(status));
    }
    public HealthStatusResponse markMedicineChecked(Long id) {
        HealthStatus hs = healthStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 건강 상태 없음"));
        hs.setCheckMedicine(true);
        return new HealthStatusResponse(healthStatusRepository.save(hs));
    }

    public HealthStatusResponse markBreakfastChecked(Long id) {
        HealthStatus hs = healthStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 건강 상태 없음"));
        hs.setCheckBreakfast(true);
        return new HealthStatusResponse(healthStatusRepository.save(hs));
    }
    public List<HealthStatusResponse> getByUserId(Long protectedUserId) {
        return healthStatusRepository.findByProtectedUserId(protectedUserId).stream()
                .map(HealthStatusResponse::new)
                .collect(Collectors.toList());
    }
    public HealthStatusResponse updateMood(Long id, MoodStatus mood) {
        HealthStatus hs = healthStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 건강 상태 없음"));

        hs.setMood(mood);

        return new HealthStatusResponse(healthStatusRepository.save(hs));
    }
}