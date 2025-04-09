package com.example.demo.controller;


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
    public ResponseEntity<CareRelation> connect(
            @RequestParam Long caregiverId,
            @RequestParam Long protectedUserId) {
        return ResponseEntity.ok(
                careRelationService.connect(caregiverId, protectedUserId)
        );
    }
    @PostMapping
    public CareRelation create(@RequestBody CareRelation relation) {
        return careRelationService.createRelation(relation);
    }


    @GetMapping("/caregiver/{caregiverId}")
    public List<CareRelation> getByCaregiver(@PathVariable Long caregiverId) {
        User caregiver = new User();
        caregiver.setId(caregiverId);
        return careRelationService.getByCaregiver(caregiver);
    }

    @GetMapping("/protected/{protectedUserId}")
    public List<CareRelation> getByProtectedUser(@PathVariable Long protectedUserId) {
        User protectedUser = new User();
        protectedUser.setId(protectedUserId);
        return careRelationService.getByProtectedUser(protectedUser);
    }
}