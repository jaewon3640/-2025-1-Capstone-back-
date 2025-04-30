package com.example.demo.DTO;

import com.example.demo.domain.HealthStatus;
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

    public HealthStatusResponse(HealthStatus status)
    {
        this.id=status.getId();
        this.type=status.getType();
        this.measurement=getMeasurement();
        this.recordedAt=status.getRecordedAt();
        this.protectedUserId=status.getProtectedUser().getId();
    }
}