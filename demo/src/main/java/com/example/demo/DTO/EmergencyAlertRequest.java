package com.example.demo.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyAlertRequest {
    private Long protectedUserId;
    private String message;
    private LocalDateTime triggeredAt;
}