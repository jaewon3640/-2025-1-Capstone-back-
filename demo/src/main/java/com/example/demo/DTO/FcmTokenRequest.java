package com.example.demo.DTO;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class FcmTokenRequest {
    private Long userId;  // User DB의 id
    private String token;
}