package com.taskmaster;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmaster.models.Category;
import com.taskmaster.models.Tag;
import com.taskmaster.models.User;
import com.taskmaster.models.dto.TaskRequest;
import com.taskmaster.repositories.CategoryRepository;
import com.taskmaster.repositories.TagRepository;
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

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class Phase5IntegrationTest {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String token;
    private Category category;
    private Tag tag1;

    @BeforeEach
    void setup() {
        mongoTemplate.getDb().drop();

        if (!userRepository.existsByUsername("phase5user")) {
            User user = new User();
            user.setUsername("phase5user");
            user.setEmail("phase5@example.com");
            user.setPassword("password");
            user.setRole("ROLE_USER");
            userRepository.save(user);
        }

        token = jwtTokenProvider.generateToken(
                new UsernamePasswordAuthenticationToken("phase5user", null, Collections.emptyList())
        );

        if (categoryRepository.findByName("Work").isEmpty()) {
            category = new Category();
            category.setName("Work");
            categoryRepository.save(category);
        } else {
            category = categoryRepository.findByName("Work").get();
        }

        if (tagRepository.findByName("Urgent").isEmpty()) {
            tag1 = new Tag();
            tag1.setName("Urgent");
            tagRepository.save(tag1);
        } else {
            tag1 = tagRepository.findByName("Urgent").get();
        }
    }

    @Test
    void testCategoryAndTagAssignmentAndFiltering() throws Exception {
        // Create Task with Category and Tag
        TaskRequest request = new TaskRequest();
        request.setTitle("Categorized Task");
        request.setDescription("Has category and tags");
        request.setCategoryId(category.getId());
        request.setTagIds(List.of(tag1.getId()));

        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryName").value("Work"))
                .andExpect(jsonPath("$.tags[0]").value("Urgent"));

        // Filter by Category
        mockMvc.perform(get("/api/tasks")
                        .param("categoryId", String.valueOf(category.getId()))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Categorized Task"));

        // Filter by Tag
        mockMvc.perform(get("/api/tasks")
                        .param("tagId", String.valueOf(tag1.getId()))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Categorized Task"));
    }
}
