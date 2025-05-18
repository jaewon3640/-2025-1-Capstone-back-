package com.example.demo.DTO;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class FcmTokenRequest {
    private String userId;
    private String token;
}