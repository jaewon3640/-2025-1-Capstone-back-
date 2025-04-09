package com.example.demo.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationRequest {
    private Long protectedUserId;
    private double latitude;
    private double longitude;
    private String status;
    private LocalDateTime timestamp;
}