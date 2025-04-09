package com.example.demo.controller;


import com.example.demo.domain.HealthStatus;
import com.example.demo.service.HealthStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/healthstatus")
@RequiredArgsConstructor
public class HealthStatusController {

    private final HealthStatusService healthStatusService;

    @PostMapping
    public HealthStatus create(@RequestBody HealthStatus healthStatus) {
        return healthStatusService.save(healthStatus);
    }

    @GetMapping("/user/{userId}")
    public List<HealthStatus> getByUser(@PathVariable Long userId) {
        return healthStatusService.findByProtectedUserId(userId);
    }
}