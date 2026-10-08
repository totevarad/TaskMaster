package com.taskmaster.controllers;

import com.taskmaster.models.dto.JwtAuthResponse;
import com.taskmaster.models.dto.LoginRequest;
import com.taskmaster.models.dto.RegisterRequest;
import com.taskmaster.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest) {
        String response = authService.registerUser(registerRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        JwtAuthResponse authResponse = authService.loginUser(loginRequest);
        return ResponseEntity.ok(authResponse);
    }
}
