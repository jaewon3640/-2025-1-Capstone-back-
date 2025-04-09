package com.example.demo.controller;


import com.example.demo.domain.Location;
import com.example.demo.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    public Location create(@RequestBody Location location) {
        return locationService.save(location);
    }

    @GetMapping("/user/{userId}")
    public List<Location> getByUser(@PathVariable Long userId) {
        return locationService.findByProtectedUserId(userId);
    }
}