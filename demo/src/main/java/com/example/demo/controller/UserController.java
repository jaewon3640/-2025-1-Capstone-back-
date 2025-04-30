package com.example.demo.controller;

import com.example.demo.DTO.UserResponse;
import com.example.demo.DTO.UserSignupRequest;
import com.example.demo.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserSignupRequest request) {
        return ResponseEntity.ok(userService.saveFromDto(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAll();
    }
}