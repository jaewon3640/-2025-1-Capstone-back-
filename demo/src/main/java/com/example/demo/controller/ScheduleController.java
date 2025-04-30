package com.example.demo.controller;


import com.example.demo.DTO.ScheduleRequest;
import com.example.demo.DTO.ScheduleResponse;
import com.example.demo.domain.Schedule;
import com.example.demo.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<ScheduleResponse> create(@RequestBody ScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.saveFromDto(request));
    }

    @GetMapping("/user/{userId}")
    public List<ScheduleResponse> getByUser(@PathVariable Long userId) {
        return scheduleService.getByUserId(userId);
    }

    @PutMapping("/{scheduleId}/complete")
    public ResponseEntity<ScheduleResponse> markAsComplete(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(scheduleService.markAsComplete(scheduleId));
    }
}