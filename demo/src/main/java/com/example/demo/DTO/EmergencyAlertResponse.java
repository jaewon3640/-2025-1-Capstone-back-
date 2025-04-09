package com.example.demo.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyAlertResponse {
    private Long id;
    private String message;
    private LocalDateTime triggeredAt;
    private boolean resolved;
    private Long protectedUserId;
}