package com.taskmaster;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmaster.models.Task;
import com.taskmaster.models.TaskStatus;
import com.taskmaster.models.User;
import com.taskmaster.models.dto.CommentRequest;
import com.taskmaster.models.dto.TeamRequest;
import com.taskmaster.repositories.TaskRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class Phase4IntegrationTest {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String tokenA;
    private User userA;
    private User userB;
    private Task sharedTask;

    @BeforeEach
    void setup() {
        mongoTemplate.getDb().drop();

        if (!userRepository.existsByUsername("usera")) {
            userA = new User();
            userA.setUsername("usera");
            userA.setEmail("a@example.com");
            userA.setPassword("password");
            userA.setRole("ROLE_USER");
            userRepository.save(userA);

            userB = new User();
            userB.setUsername("userb");
            userB.setEmail("b@example.com");
            userB.setPassword("password");
            userB.setRole("ROLE_USER");
            userRepository.save(userB);

            sharedTask = new Task();
            sharedTask.setTitle("Shared Task");
            sharedTask.setReporter(userA);
            sharedTask.setStatus(TaskStatus.OPEN);
            taskRepository.save(sharedTask);
        } else {
            userA = userRepository.findByUsername("usera").get();
            userB = userRepository.findByUsername("userb").get();
            sharedTask = taskRepository.findAll().get(0);
        }

        tokenA = jwtTokenProvider.generateToken(
                new UsernamePasswordAuthenticationToken("usera", null, Collections.emptyList())
        );
    }

    @Test
    void testTeamFlow() throws Exception {
        // Create Team
        TeamRequest teamRequest = new TeamRequest();
        teamRequest.setName("Backend Team");
        teamRequest.setDescription("Core developers");

        MvcResult createTeamResult = mockMvc.perform(post("/api/teams")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Backend Team"))
                .andReturn();

        String teamId = objectMapper.readTree(createTeamResult.getResponse().getContentAsString()).get("id").asText();

        // Add Member B to Team
        mockMvc.perform(post("/api/teams/" + teamId + "/members/" + userB.getId())
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk());

        // Get My Teams (User A)
        mockMvc.perform(get("/api/teams")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Backend Team"));
    }

    @Test
    void testTaskCollaboration() throws Exception {
        // Add Comment
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setContent("Looks good to me!");

        mockMvc.perform(post("/api/tasks/" + sharedTask.getId() + "/comments")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Looks good to me!"))
                .andExpect(jsonPath("$.username").value("usera"));

        // Get Comments
        mockMvc.perform(get("/api/tasks/" + sharedTask.getId() + "/comments")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Looks good to me!"));
    }

    @Test
    void testFileUpload() throws Exception {
        org.springframework.mock.web.MockMultipartFile file = new org.springframework.mock.web.MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart("/api/tasks/" + sharedTask.getId() + "/attachments")
                        .file(file)
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isCreated());
    }
}
