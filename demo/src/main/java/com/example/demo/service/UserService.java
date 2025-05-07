package com.example.demo.service;

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
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .role(request.getRole())
                .password(request.getPassword())
                .phone(request.getPhone())
                .build();

        return new UserResponse(userRepository.save(user));
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
