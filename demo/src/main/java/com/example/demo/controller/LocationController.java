package com.example.demo.controller;


import com.example.demo.DTO.LocationRequest;
import com.example.demo.DTO.LocationResponse;
import com.example.demo.domain.Location;
import com.example.demo.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    public ResponseEntity<LocationResponse> create(@RequestBody LocationRequest request) {
        return ResponseEntity.ok(locationService.saveFromDto(request));
    }

    @GetMapping("/protected/{protectedUserId}")
    public List<LocationResponse> getByUser(@PathVariable Long protectedUserId) {
        return locationService.getByUserId(protectedUserId);
    }
}