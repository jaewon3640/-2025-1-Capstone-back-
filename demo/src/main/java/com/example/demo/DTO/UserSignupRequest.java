package com.example.demo.DTO;

import com.example.demo.UserRole;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignupRequest {
    private String name;
    private String email;
    private String phone;
    private UserRole role;
}