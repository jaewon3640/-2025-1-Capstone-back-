package com.example.demo.DTO;

import com.example.demo.domain.Schedule;
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

    public ScheduleResponse(Schedule schedule)
    {
        this.id=schedule.getId();
        this.title=schedule.getTitle();
        this.description=schedule.getDescription();
        this.scheduledAt=schedule.getScheduledAt();
        this.completed=schedule.isCompleted();
        this.protectedUserId=schedule.getProtectedUser().getId();
    }
}