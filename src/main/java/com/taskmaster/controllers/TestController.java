package com.taskmaster.controllers;

import com.taskmaster.exceptions.ResourceNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/not-found")
    public String throwNotFound() {
        throw new ResourceNotFoundException("Test resource not found");
    }

    @GetMapping("/bad-request")
    public String throwBadRequest() {
        throw new IllegalArgumentException("Test bad request");
    }
}
