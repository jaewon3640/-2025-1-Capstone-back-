package com.example.demo.DTO.schedule.response;

import com.example.demo.domain.schedule.ScheduleType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScheduleResponse {
    private String title;
    private ScheduleType type;
    private String time;      // "오전 08시 00분"
    private boolean completed;
}
