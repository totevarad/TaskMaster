package com.taskmaster.controllers;

import com.taskmaster.models.dto.UserProfileResponse;
import com.taskmaster.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }
}
