package com.example.demo.DTO;

import com.example.demo.MoodStatus;
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
    private boolean checkMedicine;
    private boolean checkBreakfast;
    private String mood;
    private LocalDateTime recordedAt;
    private Long protectedUserId;
    private String protectedUserName;

    public HealthStatusResponse(HealthStatus status)
    {
        this.id=status.getId();
        this.checkMedicine=status.isCheckMedicine();
        this.checkBreakfast=status.isCheckBreakfast();
        this.mood=status.getMood().name();
        this.recordedAt=status.getRecordedAt();
        this.protectedUserId=status.getProtectedUser().getId();
        this.protectedUserName=status.getProtectedUser().getName();
    }
}