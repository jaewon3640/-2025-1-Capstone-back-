package com.example.demo.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HealthStatusRequest {
    private Long protectedUserId;
    private String type;
    private String measurement;
    private LocalDateTime recordedAt;
}