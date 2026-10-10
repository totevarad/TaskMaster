package com.taskmaster.services;

import com.taskmaster.exceptions.ResourceNotFoundException;
import com.taskmaster.models.Attachment;
import com.taskmaster.models.Task;
import com.taskmaster.models.User;
import com.taskmaster.repositories.AttachmentRepository;
import com.taskmaster.repositories.TaskRepository;
import com.taskmaster.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class AttachmentService {

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private final String UPLOAD_DIR = "uploads/";

    public String uploadAttachment(String taskId, MultipartFile file) throws IOException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR + fileName);
        Files.write(filePath, file.getBytes());

        Attachment attachment = new Attachment();
        attachment.setTask(task);
        attachment.setUser(user);
        attachment.setFileUrl(filePath.toString());
        attachment.setFileType(file.getContentType());

        attachmentRepository.save(attachment);

        return "File uploaded successfully: " + fileName;
    }
}
