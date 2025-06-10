package com.example.demo.DTO;

import com.example.demo.UserRole;
import com.example.demo.domain.Notification;
import com.example.demo.domain.schedule.ScheduleType;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationResponse {
    private Long id;
    private Long userId;   // User DB의 id

    private String title;
    private ScheduleType type;
    private LocalDateTime notifiedAt;
    private boolean sent;

    public NotificationResponse(Notification msg) {
        this.id = msg.getId();
        if (msg.getRole() == UserRole.보호자) {
            this.userId = msg.getCaregiverId();
        }
        else {
            this.userId = msg.getProtectedUserId();
        }

        this.title = msg.getTitle();
        this.type = msg.getType();
        this.notifiedAt = msg.getNotifiedAt();
        this.sent = msg.isSent();
    }
}