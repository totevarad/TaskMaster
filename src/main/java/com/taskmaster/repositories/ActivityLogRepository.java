package com.taskmaster.repositories;

import com.taskmaster.models.ActivityLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ActivityLogRepository extends MongoRepository<ActivityLog, String> {
    List<ActivityLog> findByEntityTypeAndEntityId(String entityType, String entityId);
}
