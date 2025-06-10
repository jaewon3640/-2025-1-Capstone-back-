package com.example.demo.DTO;

import com.example.demo.domain.FcmToken;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FcmTokenResponse {
    private Long id;
    private Long userId;  // User DB의 id
    private String token;

    public FcmTokenResponse(FcmToken token) {
        this.id = token.getId();
        this.userId = token.getUserId();
        this.token = token.getToken();
    }
}