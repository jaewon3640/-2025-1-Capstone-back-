package com.example.demo.DTO.schedule.response;

import com.example.demo.domain.schedule.ScheduleType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class RecurringScheduleResponse {
    private String title;
    private ScheduleType type;
    private String dayOfWeek;
    private String time;
    private LocalDate endDay;
}


