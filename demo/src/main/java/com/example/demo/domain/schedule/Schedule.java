package com.example.demo.domain.schedule;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "schedule")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long protectedUserId;

    private String title;

    @Enumerated(EnumType.STRING)
    private ScheduleType type;

    private boolean recurring;

    private LocalDateTime dateTime;

    private boolean completed;
}

