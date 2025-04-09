package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "health_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // 예: 혈압, 체온, 수면 등
    private String measurement;

    private LocalDateTime recordedAt;

    @ManyToOne
    @JoinColumn(name = "protected_user_id")
    private User protectedUser;
}
