package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "emergency_alerts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmergencyAlert {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime triggeredAt;

    private String message;

    private boolean resolved;

    @ManyToOne
    @JoinColumn(name = "protected_user_id")
    private User protectedUser;
}
