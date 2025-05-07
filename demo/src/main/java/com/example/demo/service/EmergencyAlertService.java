package com.example.demo.service;


import com.example.demo.DTO.EmergencyAlertRequest;
import com.example.demo.DTO.EmergencyAlertResponse;
import com.example.demo.DTO.HealthStatusRequest;
import com.example.demo.DTO.HealthStatusResponse;
import com.example.demo.domain.EmergencyAlert;
import com.example.demo.domain.HealthStatus;
import com.example.demo.domain.User;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.repository.EmergencyAlertRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmergencyAlertService {

    private final EmergencyAlertRepository emergencyAlertRepository;
    private final UserRepository userRepository;

    public EmergencyAlertResponse saveFromDto(EmergencyAlertRequest request) {
        User protectedUser = userRepository.findById(request.getProtectedUserId())
                .orElseThrow(() -> new UserNotFoundException("해당 사용자가 존재하지 않습니다."));

        EmergencyAlert alert = EmergencyAlert.builder()
                .protectedUser(protectedUser)
                .message(request.getMessage())
                .triggeredAt(request.getTriggeredAt())
                .build();

        return new EmergencyAlertResponse(emergencyAlertRepository.save(alert));
    }

    public EmergencyAlertResponse markAsResolved(Long alertId) {
        EmergencyAlert alert = emergencyAlertRepository.findById(alertId)
                .orElseThrow(() -> new UserNotFoundException("해당 알림이 존재하지 않습니다."));
        alert.setResolved(true);
        return new EmergencyAlertResponse(emergencyAlertRepository.save(alert));
    }

    public List<EmergencyAlertResponse> getAll() {
        return emergencyAlertRepository.findAll().stream()
                .map(EmergencyAlertResponse::new)
                .collect(Collectors.toList());
    }
}