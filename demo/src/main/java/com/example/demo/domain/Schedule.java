package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    private LocalDateTime scheduledAt;

    private boolean completed;

    @ManyToOne
    @JoinColumn(name = "protected_user_id")
    private User protectedUser;
}

