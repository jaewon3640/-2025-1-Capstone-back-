package com.example.demo.service;


import com.example.demo.domain.EmergencyAlert;
import com.example.demo.repository.EmergencyAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmergencyAlertService {

    private final EmergencyAlertRepository emergencyAlertRepository;

    public EmergencyAlert save(EmergencyAlert emergencyAlert) {
        return emergencyAlertRepository.save(emergencyAlert);
    }

    public List<EmergencyAlert> findByProtectedUserId(Long protectedUserId) {
        return emergencyAlertRepository.findByProtectedUserId(protectedUserId); // ✅ 수정됨
    }
}