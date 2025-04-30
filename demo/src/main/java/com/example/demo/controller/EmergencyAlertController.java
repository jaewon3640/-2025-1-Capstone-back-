package com.example.demo.controller;


import com.example.demo.DTO.EmergencyAlertRequest;
import com.example.demo.DTO.EmergencyAlertResponse;
import com.example.demo.domain.EmergencyAlert;
import com.example.demo.service.EmergencyAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emergencyalert")
@RequiredArgsConstructor
public class EmergencyAlertController {

    private final EmergencyAlertService emergencyAlertService;

    @PostMapping
    public ResponseEntity<EmergencyAlertResponse> createAlert(@RequestBody EmergencyAlertRequest request) {
        return ResponseEntity.ok(emergencyAlertService.saveFromDto(request));
    }

    @GetMapping
    public List<EmergencyAlertResponse> getAll() {
        return emergencyAlertService.getAll();
    }

    @PutMapping("/{alertId}/resolve")
    public ResponseEntity<EmergencyAlertResponse> resolveAlert(@PathVariable Long alertId) {
        return ResponseEntity.ok(emergencyAlertService.markAsResolved(alertId));
    }
}