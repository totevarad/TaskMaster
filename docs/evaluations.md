# TaskMaster: Phase-wise Evaluation & Integration Testing Plan

This document outlines the evaluation criteria and integration testing strategy for each phase of the TaskMaster backend implementation to ensure robustness, security, and correctness.

---

## Phase 1: Project Setup & Core Infrastructure

### Evaluation Criteria
*   **Application Bootup:** The Spring Boot application starts successfully without any context loading errors.
*   **Database Connection:** The application connects successfully to the configured database (MongoDB).
*   **Exception Handling:** Sending a malformed request or accessing an undefined endpoint correctly triggers the `@ControllerAdvice` and returns a standardized JSON error response rather than a generic Tomcat error page.

### Integration Testing Strategy
*   **Context Load Test:** A basic `@SpringBootTest` to ensure the Spring context loads properly.
*   **Database Connectivity Test:** Using `@DataMongoTest` or equivalent to verify that basic repository configurations can connect to a database (like Testcontainers) and perform a simple entity save/read.
*   **Exception Handler Test:** Use `MockMvc` to trigger a `404 Not Found` or `400 Bad Request` and assert that the JSON structure matches the expected custom error format.

---

## Phase 2: User Authentication & Profile Management

### Evaluation Criteria
*   **Registration:** A user can successfully register with a username, email, and password. The password must be securely hashed in the database.
*   **Login:** A registered user can log in with valid credentials and receive a JWT. Invalid credentials should return a `401 Unauthorized`.
*   **Security Context:** Accessing a protected endpoint without a JWT (or with an expired/invalid JWT) results in a `401/403` error. Accessing with a valid JWT succeeds.

### Integration Testing Strategy
*   **End-to-End Auth Flow:** 
    1.  Use `MockMvc` to `POST /api/auth/register` with test user details and assert `201 Created`.
    2.  `POST /api/auth/login` with the same details and extract the JWT from the response.
    3.  `GET /api/users/profile` using the extracted JWT in the `Authorization: Bearer <token>` header and assert the user details are returned (`200 OK`).
*   **Authentication Failure Tests:** Assert that login endpoints return `401` for bad passwords and protected endpoints return `401` when the header is missing.

---

## Phase 3: Core Task Management

### Evaluation Criteria
*   **Task CRUD:** Users can create, read, update, and delete tasks.
*   **Ownership:** A user can only see, edit, or delete tasks they own or are assigned to.
*   **Search & Filter:** API properly handles query parameters to return filtered lists of tasks (e.g., `?status=OPEN`, `?search=urgent`).

### Integration Testing Strategy
*   **CRUD Integration Test:**
    1.  Authenticate a test user and obtain a JWT.
    2.  `POST /api/tasks` with valid task data; assert `201 Created` and ID is returned.
    3.  `GET /api/tasks/{id}` to verify data integrity.
    4.  `PUT /api/tasks/{id}` to modify a field (e.g., status); assert `200 OK`.
    5.  `DELETE /api/tasks/{id}`; assert `204 No Content`.
    6.  `GET /api/tasks/{id}` to verify the task is gone (assert `404 Not Found`).
*   **Filter Test:** Pre-populate the test DB with varied tasks. Use `MockMvc` to `GET /api/tasks?status=COMPLETED` and assert that the returned JSON array contains only completed tasks.

---

## Phase 4: Team Collaboration Features

### Evaluation Criteria
*   **Team Creation:** A user can create a team and becomes its owner.
*   **Membership:** Users can be invited to and join teams.
*   **Assignment:** Tasks can be explicitly assigned to users within a specific team.
*   **Comments/Attachments:** Users can add comments to a task. File attachments can be successfully uploaded, stored, and downloaded.

### Integration Testing Strategy
*   **Team Flow Test:**
    1.  Create User A and User B.
    2.  User A creates a Team (`POST /api/teams`).
    3.  User A invites User B to the team (`POST /api/teams/{id}/members`).
    4.  Assert User B's team list now includes the new team.
*   **Task Collaboration Test:**
    1.  User A creates a task in the shared team and assigns it to User B.
    2.  User B comments on the task (`POST /api/tasks/{id}/comments`).
    3.  `GET /api/tasks/{id}/comments` to assert the comment is persisted and mapped correctly.
*   **File Upload Test:** Use `MockMultipartFile` with `MockMvc` to test the attachment upload endpoint and assert the storage logic completes without errors.

---

## Phase 5: Advanced Features & Integrations

### Evaluation Criteria
*   **WebSocket Connections:** Clients can successfully connect to the WebSocket/STOMP endpoint and subscribe to their user-specific topic.
*   **Real-time Delivery:** When a task is assigned to a user via the REST API, that user receives an event notification immediately over the active WebSocket connection.
*   **AI Integration:** Hitting the AI generation endpoint with a prompt string returns a properly structured task description within a reasonable timeout.

### Integration Testing Strategy
*   **WebSocket Integration Test:** Use Spring's `WebSocketStompClient` in a test class to connect to the broker, subscribe to a test queue, trigger a task assignment via REST, and assert the message is received on the STOMP client within `x` seconds.
*   **AI Mocking Test:** Since calling an actual external AI API during tests is slow and costly, use **WireMock** to mock the external OpenAI/Gemini server. Assert that the `AIGenerationService` correctly formats the HTTP request to the mock server and correctly parses the mocked JSON response back to the client.

---

## Phase 6: Polish, Testing, & Documentation

### Evaluation Criteria
*   **Test Coverage:** Run the test suite and ensure adequate coverage (e.g., >80% line coverage) for all service and controller classes using JaCoCo or similar tools.
*   **API Documentation:** Navigating to `/swagger-ui.html` successfully displays the interactive OpenAPI documentation, with all endpoints accurately described and testable via the UI.
*   **Validation Constraints:** Invalid data (e.g., empty titles, future dates where past dates are required) is caught by `@Valid` annotations and returns specific `400 Bad Request` messages detailing the validation failures.

### Integration Testing Strategy
*   **Validation Tests:** Write `MockMvc` tests specifically targeting DTO constraints. Send POST requests with missing required fields, extremely long strings, or invalid email formats, and assert the API responds with `400 Bad Request` and the correct validation error messages.
