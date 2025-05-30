package com.example.demo.domain;

import com.example.demo.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Enumerated(EnumType.STRING)
    private UserRole role;  // 이제 타입 안전

    private String phone;
    private String email;
    private String password;

    // 보호자가 관리하는 피보호자 목록
    @OneToMany(mappedBy = "caregiver")
    private List<CareRelation> careTargets;

    // 피보호자로서 나를 관리하는 보호자 목록
    @OneToMany(mappedBy = "protectedUser")
    private List<CareRelation> guardians;
}
