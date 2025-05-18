package com.example.demo.DTO.schedule.response;

import com.example.demo.domain.schedule.ScheduleType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TodayScheduleResponse {
    private String title;
    private ScheduleType type;
    private String time;
}