package com.example.demo.service;


import com.example.demo.domain.HealthStatus;
import com.example.demo.repository.HealthStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthStatusService {

    private final HealthStatusRepository healthStatusRepository;

    public HealthStatus save(HealthStatus healthStatus) {
        return healthStatusRepository.save(healthStatus);
    }

    public List<HealthStatus> findByProtectedUserId(Long protectedUserId) {
        return healthStatusRepository.findByProtectedUserId(protectedUserId); // ✅ 수정됨
    }
}