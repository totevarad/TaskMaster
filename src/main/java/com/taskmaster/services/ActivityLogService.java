package com.taskmaster.services;

import com.taskmaster.models.ActivityLog;
import com.taskmaster.repositories.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityLogService {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    public void logActivity(String entityType, String entityId, String action, String description) {
        ActivityLog log = new ActivityLog();
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setAction(action);
        log.setDescription(description);
        activityLogRepository.save(log);
    }

    public List<ActivityLog> getLogsForEntity(String entityType, String entityId) {
        return activityLogRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }
}
