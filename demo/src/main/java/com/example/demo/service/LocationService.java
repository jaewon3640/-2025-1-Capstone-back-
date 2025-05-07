package com.example.demo.service;


import com.example.demo.DTO.LocationRequest;
import com.example.demo.DTO.LocationResponse;
import com.example.demo.domain.HealthStatus;
import com.example.demo.domain.Location;
import com.example.demo.domain.User;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.repository.LocationRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    public LocationResponse saveFromDto(LocationRequest request) {

        User protectedUser = userRepository.findById(request.getProtectedUserId())
                .orElseThrow(() -> new UserNotFoundException("해당 사용자가 존재하지 않습니다."));
        Location location = Location.builder()
                .protectedUser(protectedUser)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .timestamp(request.getTimestamp())
                .status(request.getStatus())
                .build();

        return new LocationResponse(locationRepository.save(location));
    }

    public List<LocationResponse> getByUserId(Long userId) {
        return locationRepository.findByProtectedUserId(userId).stream()
                .map(LocationResponse::new)
                .collect(Collectors.toList());
    }
}