package com.taskmaster.controllers;

import com.taskmaster.services.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/tasks/{taskId}/attachments")
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;

    @PostMapping
    public ResponseEntity<String> uploadAttachment(
            @PathVariable String taskId,
            @RequestParam("file") MultipartFile file) {
        try {
            String message = attachmentService.uploadAttachment(taskId, file);
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to upload file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
