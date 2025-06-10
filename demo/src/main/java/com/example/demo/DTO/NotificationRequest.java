package com.example.demo.DTO;

import com.example.demo.domain.schedule.ScheduleType;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationRequest {
    private Long id;
    private Long userId;   // User DB의 id

    private String title;
    private ScheduleType type;
    private LocalDateTime notifiedAt;
}