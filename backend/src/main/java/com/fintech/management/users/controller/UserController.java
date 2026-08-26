package com.fintech.management.users.controller;

import com.fintech.management.users.dto.LoginRequest;
import com.fintech.management.users.dto.LoginResponse;
import com.fintech.management.users.dto.UserDTO;
import com.fintech.management.users.dto.UserRegistrationRequest;
import com.fintech.management.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody UserRegistrationRequest request) {
        UserDTO createdUser = userService.registerUser(request);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/by-login/{login}")
    public ResponseEntity<UserDTO> getByLogin(@PathVariable String login) {
        return ResponseEntity.ok(userService.getUserByLogin(login));
    }

    // El endpoint de Login que usaremos en Postman
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable UUID id, @RequestBody UserRegistrationRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }
}