package com.example.demo.service;


import com.example.demo.domain.HealthStatus;
import com.example.demo.domain.Location;
import com.example.demo.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    public Location save(Location location) {
        return locationRepository.save(location);
    }

    public List<Location> findByProtectedUserId(Long protectedUserId) {
        return locationRepository.findByProtectedUserId(protectedUserId); // ✅ 수정됨
    }
}