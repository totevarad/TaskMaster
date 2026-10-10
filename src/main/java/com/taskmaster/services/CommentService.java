package com.taskmaster.services;

import com.taskmaster.exceptions.ResourceNotFoundException;
import com.taskmaster.models.Comment;
import com.taskmaster.models.Task;
import com.taskmaster.models.User;
import com.taskmaster.models.dto.CommentRequest;
import com.taskmaster.models.dto.CommentResponse;
import com.taskmaster.repositories.CommentRepository;
import com.taskmaster.repositories.TaskRepository;
import com.taskmaster.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    public CommentResponse addComment(String taskId, CommentRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        Comment comment = new Comment();
        comment.setTask(task);
        comment.setUser(user);
        comment.setContent(request.getContent());

        Comment savedComment = commentRepository.save(comment);
        return mapToResponse(savedComment);
    }

    public List<CommentResponse> getCommentsForTask(String taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task not found");
        }

        return commentRepository.findByTaskId(taskId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private CommentResponse mapToResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setTaskId(comment.getTask().getId());
        response.setUserId(comment.getUser().getId());
        response.setUsername(comment.getUser().getUsername());
        response.setCreatedAt(comment.getCreatedAt());
        return response;
    }
}
