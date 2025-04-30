package com.example.demo.service;

import com.example.demo.DTO.UserSignupRequest;
import com.example.demo.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.demo.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User signup(UserSignupRequest request) {
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passward(request.getPassward())
                .phone(request.getPhone())
                .role(request.getRole())
                .build();

        return userRepository.save(user);
    }
    public boolean isEmailDuplicated(String email) {
        return userRepository.existsByEmail(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
