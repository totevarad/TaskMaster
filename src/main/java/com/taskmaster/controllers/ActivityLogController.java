package com.taskmaster.controllers;

import com.taskmaster.models.ActivityLog;
import com.taskmaster.services.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity-logs")
public class ActivityLogController {

    @Autowired
    private ActivityLogService activityLogService;

    @GetMapping
    public ResponseEntity<List<ActivityLog>> getLogs(
            @RequestParam String entityType,
            @RequestParam String entityId) {
        return ResponseEntity.ok(activityLogService.getLogsForEntity(entityType, entityId));
    }
}
