package com.example.demo.controller;


import com.example.demo.domain.Schedule;
import com.example.demo.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public Schedule create(@RequestBody Schedule schedule) {
        return scheduleService.save(schedule);
    }

    @GetMapping("/user/{userId}")
    public List<Schedule> getByUser(@PathVariable Long userId) {
        return scheduleService.findByProtectedUserId(userId);
    }
}