package com.example.demo.domain.schedule;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Table(name = "recurringSchedule")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecurringSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long protectedUserId;

    private String title;

    @Enumerated(EnumType.STRING)
    private ScheduleType type;

    private List<String> dayOfWeek;

    private List<String> time;

    private LocalDate endDay;
}
