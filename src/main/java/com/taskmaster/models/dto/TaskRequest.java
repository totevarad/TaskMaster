package com.taskmaster.models.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public class TaskRequest {
    @NotBlank
    private String title;
    
    private String description;
    
    private LocalDate dueDate;
    
    private String assigneeId;
    
    private String categoryId;
    
    private java.util.List<String> tagIds;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getAssigneeId() { return assigneeId; }
    public void setAssigneeId(String assigneeId) { this.assigneeId = assigneeId; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public java.util.List<String> getTagIds() { return tagIds; }
    public void setTagIds(java.util.List<String> tagIds) { this.tagIds = tagIds; }
}
