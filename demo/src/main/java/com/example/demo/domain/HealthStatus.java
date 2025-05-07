package com.example.demo.domain;

import com.example.demo.MoodStatus;
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

    // 약 복용 확인여부
    private boolean checkMedicine;

    //아침식사 여부
    private boolean checkBreakfast;

    @Enumerated(EnumType.STRING) //enum 타입을 데이터 베이스에 어떻게 저장?
    private MoodStatus mood;  // 새로 추가된 기분 상태

    private LocalDateTime recordedAt;
    // 언제 기록이 되었는가?

    @ManyToOne
    @JoinColumn(name = "protected_user_id")
    private User protectedUser;
}
