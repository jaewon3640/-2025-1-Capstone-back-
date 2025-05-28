package com.example.demo.domain;

import com.example.demo.UserRole;
import com.example.demo.domain.schedule.ScheduleType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private Long caregiverId;
    private Long protectedUserId;
    private String token;
    private Long scheduleId;
    private String title;

    @Enumerated(EnumType.STRING)
    private ScheduleType type;

    private String msgTitle;
    private String body;
    private LocalDateTime notifiedAt;
    private boolean sent;
}