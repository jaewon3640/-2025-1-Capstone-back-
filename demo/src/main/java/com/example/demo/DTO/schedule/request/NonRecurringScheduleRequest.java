package com.example.demo.DTO.schedule.request;

import com.example.demo.domain.schedule.ScheduleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NonRecurringScheduleRequest {

    private Long protectedUserId;
    private String title;
    private ScheduleType type;
    private LocalDateTime dateTime;
}