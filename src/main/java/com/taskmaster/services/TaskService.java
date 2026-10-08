package com.taskmaster.services;

import com.taskmaster.exceptions.ResourceNotFoundException;
import com.taskmaster.models.Task;
import com.taskmaster.models.TaskStatus;
import com.taskmaster.models.User;
import com.taskmaster.models.dto.TaskRequest;
import com.taskmaster.models.dto.TaskResponse;
import com.taskmaster.models.Category;
import com.taskmaster.models.Tag;
import com.taskmaster.repositories.TaskRepository;
import com.taskmaster.repositories.UserRepository;
import com.taskmaster.repositories.CategoryRepository;
import com.taskmaster.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private NotificationService notificationService;

    public TaskResponse createTask(TaskRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User reporter = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Reporter not found"));

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setReporter(reporter);

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
            task.setAssignee(assignee);
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            task.setCategory(category);
        }

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(request.getTagIds());
            task.setTags(new HashSet<>(tags));
        }

        Task savedTask = taskRepository.save(task);

        activityLogService.logActivity("TASK", savedTask.getId(), "CREATED", "Task created by " + reporter.getUsername());

        if (savedTask.getAssignee() != null) {
            notificationService.createNotification(savedTask.getAssignee(), "You have been assigned a new task: " + savedTask.getTitle());
        }

        return mapToResponse(savedTask);
    }

    public TaskResponse getTaskById(String id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        return mapToResponse(task);
    }

    public List<TaskResponse> getAllTasks(String status, String search, String categoryId, String tagId) {
        List<Task> tasks = taskRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        
        return tasks.stream().filter(t -> {
            boolean match = true;
            if (status != null && !status.isEmpty()) {
                match = match && t.getStatus() == TaskStatus.valueOf(status.toUpperCase());
            }
            if (search != null && !search.isEmpty()) {
                String s = search.toLowerCase();
                boolean titleMatch = t.getTitle() != null && t.getTitle().toLowerCase().contains(s);
                boolean descMatch = t.getDescription() != null && t.getDescription().toLowerCase().contains(s);
                match = match && (titleMatch || descMatch);
            }
            if (categoryId != null) {
                match = match && t.getCategory() != null && categoryId.equals(t.getCategory().getId());
            }
            if (tagId != null) {
                match = match && t.getTags() != null && t.getTags().stream().anyMatch(tag -> tagId.equals(tag.getId()));
            }
            return match;
        }).map(this::mapToResponse).collect(Collectors.toList());
    }

    public TaskResponse updateTask(String id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
            task.setAssignee(assignee);
        } else {
            task.setAssignee(null);
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            task.setCategory(category);
        } else {
            task.setCategory(null);
        }

        if (request.getTagIds() != null) {
            List<Tag> tags = tagRepository.findAllById(request.getTagIds());
            task.setTags(new HashSet<>(tags));
        } else {
            task.getTags().clear();
        }

        Task updatedTask = taskRepository.save(task);

        activityLogService.logActivity("TASK", updatedTask.getId(), "UPDATED", "Task details updated");
        
        if (updatedTask.getAssignee() != null) {
            notificationService.createNotification(updatedTask.getAssignee(), "Task updated: " + updatedTask.getTitle());
        }

        return mapToResponse(updatedTask);
    }

    public TaskResponse updateTaskStatus(String id, String statusString) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        task.setStatus(TaskStatus.valueOf(statusString.toUpperCase()));
        Task updatedTask = taskRepository.save(task);

        activityLogService.logActivity("TASK", updatedTask.getId(), "STATUS_CHANGED", "Task status changed to " + statusString);
        
        if (updatedTask.getAssignee() != null) {
            notificationService.createNotification(updatedTask.getAssignee(), "Task status changed to " + statusString + ": " + updatedTask.getTitle());
        }

        return mapToResponse(updatedTask);
    }

    public void deleteTask(String id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        taskRepository.delete(task);
    }

    private TaskResponse mapToResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus().name());
        response.setDueDate(task.getDueDate());
        response.setCreatedAt(task.getCreatedAt());

        if (task.getAssignee() != null) {
            response.setAssigneeId(task.getAssignee().getId());
            response.setAssigneeName(task.getAssignee().getUsername());
        }
        
        response.setReporterId(task.getReporter().getId());
        response.setReporterName(task.getReporter().getUsername());
        
        if (task.getCategory() != null) {
            response.setCategoryId(task.getCategory().getId());
            response.setCategoryName(task.getCategory().getName());
        }

        if (task.getTags() != null && !task.getTags().isEmpty()) {
            response.setTags(task.getTags().stream().map(Tag::getName).collect(Collectors.toList()));
        }

        return response;
    }
}
