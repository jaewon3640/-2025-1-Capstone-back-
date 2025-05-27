package com.example.demo.domain;

import com.example.demo.UserRole;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
public class FcmToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;  // User DB의 id

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private String token;
}