package com.taskmaster;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmaster.models.User;
import com.taskmaster.models.dto.TaskRequest;
import com.taskmaster.repositories.UserRepository;
import com.taskmaster.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class Phase3IntegrationTest {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String token;
    private User testUser;

    @BeforeEach
    void setup() {
        mongoTemplate.getDb().drop();

        if (!userRepository.existsByUsername("taskuser")) {
            testUser = new User();
            testUser.setUsername("taskuser");
            testUser.setEmail("task@example.com");
            testUser.setPassword("hashedpassword");
            testUser.setRole("ROLE_USER");
            userRepository.save(testUser);
        } else {
            testUser = userRepository.findByUsername("taskuser").get();
        }

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "taskuser", null, Collections.emptyList()
        );
        token = jwtTokenProvider.generateToken(auth);
    }

    @Test
    void testTaskCRUDAndFilter() throws Exception {
        // 1. Create Task
        TaskRequest request = new TaskRequest();
        request.setTitle("Initial Task");
        request.setDescription("This is a test task");

        MvcResult createResult = mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Initial Task"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andReturn();

        String taskId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        // 2. Read Task
        mockMvc.perform(get("/api/tasks/" + taskId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("This is a test task"));

        // 3. Update Task Status
        mockMvc.perform(patch("/api/tasks/" + taskId + "/status")
                        .param("status", "COMPLETED")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        // 4. Search & Filter
        mockMvc.perform(get("/api/tasks")
                        .param("status", "COMPLETED")
                        .param("search", "Initial")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Initial Task"));

        // 5. Delete Task
        mockMvc.perform(delete("/api/tasks/" + taskId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        // 6. Verify Deletion
        mockMvc.perform(get("/api/tasks/" + taskId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
