package com.example.demo.DTO.schedule.response;

import com.example.demo.domain.schedule.ScheduleType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class NonRecurringScheduleResponse {
    private String title;
    private ScheduleType type;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateTime;
}
