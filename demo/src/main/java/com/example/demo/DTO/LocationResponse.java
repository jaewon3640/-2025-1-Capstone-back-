package com.example.demo.DTO;

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
}