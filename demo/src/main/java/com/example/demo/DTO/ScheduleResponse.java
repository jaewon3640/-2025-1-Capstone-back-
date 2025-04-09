package com.example.demo.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime scheduledAt;
    private boolean completed;
    private Long protectedUserId;
}