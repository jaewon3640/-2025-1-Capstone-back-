package com.example.demo.service;

import com.example.demo.DTO.LoginResponse;
import com.example.demo.DTO.UserResponse;
import com.example.demo.DTO.UserSignupRequest;
import com.example.demo.domain.User;
import com.example.demo.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.demo.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse saveFromDto(UserSignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .role(request.getRole())
                .password(request.getPassword())
                .phone(request.getPhone())
                .build();

        return new UserResponse(userRepository.save(user));
    }
    public LoginResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일입니다."));

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        return new LoginResponse(user);
    }
    public UserResponse getById(Long id) {
        return userRepository.findById(id)
                .map(UserResponse::new)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }
}
