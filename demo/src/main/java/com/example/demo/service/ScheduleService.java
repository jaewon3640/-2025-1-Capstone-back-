package com.example.demo.service;


import com.example.demo.DTO.ScheduleRequest;
import com.example.demo.DTO.ScheduleResponse;
import com.example.demo.domain.HealthStatus;
import com.example.demo.domain.Schedule;
import com.example.demo.domain.User;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.repository.ScheduleRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    public ScheduleResponse saveFromDto(ScheduleRequest request) {
        User protectedUser = userRepository.findById(request.getProtectedUserId())
                .orElseThrow(() -> new UserNotFoundException("해당 사용자가 존재하지 않습니다."));

        Schedule schedule = Schedule.builder()
                .protectedUser(protectedUser)
                .title(request.getTitle())
                .scheduledAt(request.getScheduledAt())
                .description(request.getDescription())
                .build();

        return new ScheduleResponse(scheduleRepository.save(schedule));
    }
    public ScheduleResponse markAsComplete(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new UserNotFoundException("일정이 존재하지 않습니다."));
        schedule.setCompleted(true);
        return new ScheduleResponse(scheduleRepository.save(schedule));
    }

    public List<ScheduleResponse> getByUserId(Long userId) {
        return scheduleRepository.findByProtectedUserId(userId).stream()
                .map(ScheduleResponse::new)
                .collect(Collectors.toList());
    }
}