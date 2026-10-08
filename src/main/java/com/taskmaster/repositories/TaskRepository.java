package com.taskmaster.repositories;

import com.taskmaster.models.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface TaskRepository extends MongoRepository<Task, String> {
    List<Task> findByAssigneeId(String assigneeId);
    List<Task> findByReporterId(String reporterId);
}
