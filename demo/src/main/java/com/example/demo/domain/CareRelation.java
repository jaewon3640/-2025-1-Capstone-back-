package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "care_relation")
public class CareRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 보호자
    @ManyToOne
    @JoinColumn(name = "caregiver_id")
    private User caregiver;

    // 피보호자
    @ManyToOne
    @JoinColumn(name = "protected_user_id")
    private User protectedUser;
}