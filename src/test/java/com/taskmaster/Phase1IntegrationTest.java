package com.taskmaster;

import org.springframework.data.mongodb.core.MongoTemplate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class Phase1IntegrationTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @org.junit.jupiter.api.BeforeEach
    void setupMongo() {
        mongoTemplate.getDb().drop();
    }


    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
        // Test passes if the application context loads successfully
    }

    @Test
    void testResourceNotFoundException() throws Exception {
        mockMvc.perform(get("/api/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Test resource not found"))
                .andExpect(jsonPath("$.path").value("/api/test/not-found"));
    }

    @Test
    void testIllegalArgumentException() throws Exception {
        mockMvc.perform(get("/api/test/bad-request"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Test bad request"))
                .andExpect(jsonPath("$.path").value("/api/test/bad-request"));
    }

}
