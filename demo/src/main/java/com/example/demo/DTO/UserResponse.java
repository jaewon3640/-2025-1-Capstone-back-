package com.example.demo.DTO;

import com.example.demo.domain.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role;

    public UserResponse(User user)
    {
        this.id= user.getId();
        this.name=user.getName();
        this.email= user.getEmail();
        this.phone= user.getPhone();
        this.role=user.getRole().name();
    }
}