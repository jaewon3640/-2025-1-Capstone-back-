package com.example.demo.DTO;

import com.example.demo.MoodStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HealthStatusRequest {

    private boolean checkMedicine;
    private boolean checkBreakfast;
    private MoodStatus mood;
    private LocalDateTime recordedAt;
    private Long protectedUserId;  // 사용자 ID만 받음 (엔티티 전체 X)
}