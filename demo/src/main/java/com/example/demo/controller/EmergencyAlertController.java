package com.example.demo.controller;


import com.example.demo.domain.EmergencyAlert;
import com.example.demo.service.EmergencyAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emergencyalert")
@RequiredArgsConstructor
public class EmergencyAlertController {

    private final EmergencyAlertService emergencyAlertService;

    @PostMapping
    public EmergencyAlert create(@RequestBody EmergencyAlert emergencyAlert) {
        return emergencyAlertService.save(emergencyAlert);
    }

    @GetMapping("/user/{userId}")
    public List<EmergencyAlert> getByUser(@PathVariable Long userId) {
        return emergencyAlertService.findByProtectedUserId(userId);
    }
}