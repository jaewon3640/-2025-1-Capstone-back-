package com.example.demo.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequest {
    private Long protectedUserId;
    private String title;
    private String description;
    private LocalDateTime scheduledAt;
}