package com.example.demo.DTO;

import com.example.demo.domain.Notification;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class NotificationResponse {
    private Long numId;
    private String userId;
    private String title;
    private String body;

    public NotificationResponse(Notification msg) {
        this.numId = msg.getNumId();
        this.userId = msg.getUserId();
        this.title = msg.getTitle();
        this.body = msg.getBody();
    }
}