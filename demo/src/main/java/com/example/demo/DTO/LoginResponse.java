package com.example.demo.DTO;
import com.example.demo.domain.User;
import lombok.*;

// LoginResponse.java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private Long id;
    private String name;
    private String role;
    private String message;

    public LoginResponse(User user)
    {
        this.id=user.getId();
        this.name=user.getName();
        this.role=user.getRole().name();
        this.message="로그인 성공";
    }
}




