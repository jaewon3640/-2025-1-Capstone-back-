package com.example.demo.controller;


import com.example.demo.DTO.HealthStatusRequest;
import com.example.demo.DTO.HealthStatusResponse;
import com.example.demo.DTO.ScheduleResponse;
import com.example.demo.domain.HealthStatus;
import com.example.demo.service.HealthStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/healthstatus")
@RequiredArgsConstructor
public class HealthStatusController {

    private final HealthStatusService healthStatusService;

    @PostMapping
    public ResponseEntity<HealthStatusResponse> create(@RequestBody HealthStatusRequest request) {
        return ResponseEntity.ok(healthStatusService.saveFromDto(request));
    }

    @GetMapping("/protected/{protectedUserId}")
    public List<HealthStatusResponse> getByUser(@PathVariable Long protectedUserId) {
        return healthStatusService.getByUserId(protectedUserId);
    }

    @PutMapping("/health-status/{id}/check-medicine")
    public ResponseEntity<HealthStatusResponse> checkMedicine(@PathVariable Long id) {
        return ResponseEntity.ok(healthStatusService.markMedicineChecked(id));
    }

    @PutMapping("/health-status/{id}/check-breakfast")
    public ResponseEntity<HealthStatusResponse> checkBreakfast(@PathVariable Long id) {
        return ResponseEntity.ok(healthStatusService.markBreakfastChecked(id));
    }
}