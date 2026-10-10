package com.taskmaster.controllers;

import com.taskmaster.models.dto.CommentRequest;
import com.taskmaster.models.dto.CommentResponse;
import com.taskmaster.services.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable String taskId,
            @Valid @RequestBody CommentRequest request) {
        return new ResponseEntity<>(commentService.addComment(taskId, request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable String taskId) {
        return ResponseEntity.ok(commentService.getCommentsForTask(taskId));
    }
}
