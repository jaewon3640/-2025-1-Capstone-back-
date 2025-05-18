package com.example.demo.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationRequest {

    private double latitude;
    private double longitude;
    private LocalDateTime timestamp;
    private Long protectedUserId;
}
