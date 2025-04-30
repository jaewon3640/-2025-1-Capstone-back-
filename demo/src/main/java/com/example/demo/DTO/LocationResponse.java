package com.example.demo.DTO;

import com.example.demo.domain.Location;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationResponse {
    private Long id;
    private double latitude;
    private double longitude;
    private String status;
    private LocalDateTime timestamp;
    private Long protectedUserId;

    public LocationResponse(Location location)
    {
        this.id=location.getId();
        this.latitude=location.getLatitude();
        this.longitude=location.getLongitude();
        this.status=location.getStatus();
        this.timestamp=location.getTimestamp();
        this.protectedUserId=location.getProtectedUser().getId();
    }
}