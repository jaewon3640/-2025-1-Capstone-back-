package com.example.demo.DTO.schedule.request;

import com.example.demo.domain.schedule.ScheduleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecurringScheduleRequest {

    private Long protectedUserId;
    private String title;
    private ScheduleType type;
    private List<DayOfWeek> dayOfWeek;
    private List<LocalTime> time;
    private LocalDate endDay;
}
