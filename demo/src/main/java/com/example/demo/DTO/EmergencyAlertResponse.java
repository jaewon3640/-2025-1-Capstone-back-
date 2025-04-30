package com.example.demo.DTO;

import com.example.demo.domain.EmergencyAlert;
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

    public EmergencyAlertResponse(EmergencyAlert alert)
    {
        this.id=alert.getId();
        this.message=alert.getMessage();
        this.triggeredAt=alert.getTriggeredAt();
        this.resolved= alert.isResolved();
        this.protectedUserId=alert.getProtectedUser().getId();
    }
}