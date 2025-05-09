package com.example.demo.DTO;

import com.example.demo.domain.FcmToken;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class FcmTokenResponse {
    private Long numId;
    private String userId;
    private String token;

    public FcmTokenResponse(FcmToken token) {
        this.numId = token.getNumId();
        this.userId = token.getUserId();
        this.token = token.getToken();
    }
}