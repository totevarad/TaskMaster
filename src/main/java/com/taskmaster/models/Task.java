package com.taskmaster.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Document

public class Task {

    @Id
    
    private String id;

    
    private String title;

    
    private String description;

    
    
    private TaskStatus status = TaskStatus.OPEN;

    
    private LocalDate dueDate;

    @DBRef
    
    private User assignee;

    @DBRef
    
    private User reporter;

    @DBRef
    
    private Category category;

    @DBRef
    
    private java.util.Set<Tag> tags = new java.util.HashSet<>();

    
    private LocalDateTime createdAt = LocalDateTime.now();

    
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = TaskStatus.OPEN;
        }
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public User getAssignee() { return assignee; }
    public void setAssignee(User assignee) { this.assignee = assignee; }

    public User getReporter() { return reporter; }
    public void setReporter(User reporter) { this.reporter = reporter; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public java.util.Set<Tag> getTags() { return tags; }
    public void setTags(java.util.Set<Tag> tags) { this.tags = tags; }
}
