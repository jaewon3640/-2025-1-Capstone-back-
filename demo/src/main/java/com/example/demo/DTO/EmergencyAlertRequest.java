package com.example.demo.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyAlertRequest {

    private String message;
    private LocalDateTime triggeredAt;
    private Long protectedUserId;
}