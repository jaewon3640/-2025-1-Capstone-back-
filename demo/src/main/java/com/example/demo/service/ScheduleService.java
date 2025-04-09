package com.example.demo.service;


import com.example.demo.domain.HealthStatus;
import com.example.demo.domain.Schedule;
import com.example.demo.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public Schedule save(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public List<Schedule> findByProtectedUserId(Long protectedUserId) {
        return scheduleRepository.findByProtectedUserId(protectedUserId); // ✅ 수정됨
    }
}