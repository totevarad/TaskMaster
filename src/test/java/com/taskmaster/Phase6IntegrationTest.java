package com.taskmaster;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmaster.models.Task;
import com.taskmaster.models.TaskStatus;
import com.taskmaster.models.User;
import com.taskmaster.repositories.TaskRepository;
import com.taskmaster.repositories.UserRepository;
import com.taskmaster.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class Phase6IntegrationTest {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String tokenOwner;
    private String tokenAssignee;
    private User owner;
    private User assignee;
    private Task task;

    @BeforeEach
    void setup() {
        mongoTemplate.getDb().drop();

        if (!userRepository.existsByUsername("ownerUser")) {
            owner = new User();
            owner.setUsername("ownerUser");
            owner.setEmail("owner@example.com");
            owner.setPassword("pass");
            owner.setRole("ROLE_USER");
            userRepository.save(owner);

            assignee = new User();
            assignee.setUsername("assigneeUser");
            assignee.setEmail("assignee@example.com");
            assignee.setPassword("pass");
            assignee.setRole("ROLE_USER");
            userRepository.save(assignee);

            task = new Task();
            task.setTitle("Log Test Task");
            task.setReporter(owner);
            task.setAssignee(assignee);
            task.setStatus(TaskStatus.OPEN);
            taskRepository.save(task);
        } else {
            owner = userRepository.findByUsername("ownerUser").get();
            assignee = userRepository.findByUsername("assigneeUser").get();
            task = taskRepository.findAll().get(0);
        }

        tokenOwner = jwtTokenProvider.generateToken(
                new UsernamePasswordAuthenticationToken("ownerUser", null, Collections.emptyList())
        );

        tokenAssignee = jwtTokenProvider.generateToken(
                new UsernamePasswordAuthenticationToken("assigneeUser", null, Collections.emptyList())
        );
    }

    @Test
    void testLoggingAndNotification() throws Exception {
        // Change Status (Trigger log & notification)
        mockMvc.perform(patch("/api/tasks/" + task.getId() + "/status")
                        .param("status", "IN_PROGRESS")
                        .header("Authorization", "Bearer " + tokenOwner))
                .andExpect(status().isOk());

        // Verify Activity Log for this task
        mockMvc.perform(get("/api/activity-logs")
                        .param("entityType", "TASK")
                        .param("entityId", String.valueOf(task.getId()))
                        .header("Authorization", "Bearer " + tokenOwner))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].action").value("STATUS_CHANGED"));

        // Verify Notification for the assignee
        mockMvc.perform(get("/api/notifications")
                        .header("Authorization", "Bearer " + tokenAssignee))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("Task status changed to IN_PROGRESS: Log Test Task"));
    }
}
