package com.example.demo.DTO;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class NotificationRequest {
    private Long numId;
    private String userId;
    private String title;
    private String body;
}