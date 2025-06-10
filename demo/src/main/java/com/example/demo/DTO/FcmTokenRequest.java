package com.example.demo.DTO;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FcmTokenRequest {
    private Long userId;  // User DB의 id
    private String token;
}