package com.example.demo.controller;


import com.example.demo.DTO.CareRelationRequest;
import com.example.demo.DTO.CareRelationResponse;
import com.example.demo.domain.CareRelation;
import com.example.demo.domain.User;
import com.example.demo.service.CareRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/care-relations")
@RequiredArgsConstructor
public class CareRelationController {

    private final CareRelationService careRelationService;

    @PostMapping("/connect")
    public ResponseEntity<CareRelationResponse> connect(@RequestBody CareRelationRequest request) {
        return ResponseEntity.ok(careRelationService.connectFromDto(request));
    }

    @PostMapping
    public ResponseEntity<CareRelationResponse> create(@RequestBody CareRelationRequest request) {
        return ResponseEntity.ok(careRelationService.createRelationFromDto(request));
    }

    @GetMapping("/caregiver/{caregiverId}")
    public List<CareRelationResponse> getByCaregiver(@PathVariable Long caregiverId) {
        return careRelationService.getByCaregiverId(caregiverId);
    }

    @GetMapping("/protected/{protectedUserId}")
    public List<CareRelationResponse> getByProtectedUser(@PathVariable Long protectedUserId) {
        return careRelationService.getByProtectedUserId(protectedUserId);
    }
}