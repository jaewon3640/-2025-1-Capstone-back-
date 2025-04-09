package com.example.demo.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthStatusResponse {
    private Long id;
    private String type;
    private String measurement;
    private LocalDateTime recordedAt;
    private Long protectedUserId;
}