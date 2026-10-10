# TaskMaster Backend

TaskMaster is a comprehensive Task Tracking and Collaboration System built with **Spring Boot**. It is designed to manage tasks efficiently, collaborate with team members, organize work through categories and tags, and track system activity with robust logging and notifications.

---

## 🚀 Features

- **User Authentication & Profile Management**
  - Secure registration and login using JWT (JSON Web Tokens).
  - Encrypted passwords using BCrypt.
- **Core Task Management**
  - Full CRUD capabilities for Tasks (Create, Read, Update, Delete).
  - Assign tasks to users, set due dates, and update statuses (`OPEN`, `IN_PROGRESS`, `COMPLETED`).
  - Advanced filtering and searching (by title, description, status, category, tag).
- **Team Collaboration**
  - Create teams and invite members.
  - Leave comments on tasks for active discussion.
  - Upload file attachments to tasks.
- **Categorization & Tags**
  - Group tasks under specific Categories (1:N relationship).
  - Label tasks with multiple Tags (M:N relationship).
- **Notifications & Activity Logging**
  - Automated activity logs tracking every status change or update.
  - In-app notifications generated automatically for assigned users.
- **Global Error Handling**
  - Standardized JSON error responses and custom exception handling.

---

## 🛠️ Tech Stack

- **Java 21**
- **Spring Boot 3.4.1**
  - Spring Web
  - Spring Data MongoDB
  - Spring Security
  - Spring Boot Validation
- **Database**: MongoDB
- **Security**: JWT (`jjwt` library)
- **Build Tool**: Gradle 9.7.1

---

## 📋 Prerequisites

Before you begin, ensure you have the following installed:
- [Java Development Kit (JDK) 21](https://jdk.java.net/21/) or higher.
- [MongoDB](https://www.mongodb.com/) database server.

---

## ⚙️ Setup and Configuration

1. **Clone the repository** (if applicable) or navigate to the project root directory.

2. **Configure the Database:**
   - Ensure your local MongoDB instance is running.
   - Update your `src/main/resources/application.yml` (or `application.properties`) with your database URI:
     ```yaml
     spring:
       data:
         mongodb:
           uri: mongodb://localhost:27017/taskmaster
     ```

3. **Configure JWT Secret (Optional but Recommended):**
   - You can override the default JWT secret and expiration in your properties file:
     ```yaml
     app:
       jwt:
         secret: your_super_secure_secret_key_which_must_be_at_least_32_characters_long
         expiration-milliseconds: 86400000
     ```

---

## ▶️ Running the Application

To start the application locally, run the following Gradle wrapper command from the root directory:

**On Windows:**
```powershell
.\gradlew.bat bootRun
```

**On macOS/Linux:**
```bash
./gradlew bootRun
```

The server will start on port `8080` by default. You can access the API at `http://localhost:8080`.

---

## 🧪 Running Tests

The project includes an extensive suite of integration tests that evaluate all business logic and security layers. The tests utilize Spring Boot's test capabilities. Make sure a MongoDB instance is available, or configure an embedded MongoDB/Testcontainers for testing.

To run the test suite:

**On Windows:**
```powershell
.\gradlew.bat clean test
```

**On macOS/Linux:**
```bash
./gradlew clean test
```

---

## 📖 API Endpoints Overview

When running the application locally on the default port, the base URL for all API requests is:
**`http://localhost:8080`**

For example, to register a new user locally, you would send a `POST` request to:
`http://localhost:8080/api/auth/register`

Here is a quick overview of the main REST endpoints (append these to the base URL):

### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login and receive a JWT

### Users & Teams
- `GET /api/users/profile` - Get current user profile
- `POST /api/teams` - Create a new team
- `GET /api/teams` - List your teams
- `POST /api/teams/{teamId}/members/{userId}` - Add a user to a team

### Tasks
- `POST /api/tasks` - Create a task
- `GET /api/tasks` - List tasks (Supports query params: `status`, `search`, `categoryId`, `tagId`)
- `GET /api/tasks/{id}` - Get task details
- `PUT /api/tasks/{id}` - Update a task
- `PATCH /api/tasks/{id}/status?status=...` - Update task status
- `DELETE /api/tasks/{id}` - Delete a task

### Collaboration
- `POST /api/tasks/{taskId}/comments` - Add a comment
- `GET /api/tasks/{taskId}/comments` - View comments
- `POST /api/tasks/{taskId}/attachments` - Upload a file (multipart form data)

### Categories & Tags
- `POST /api/categories` - Create a category
- `GET /api/categories` - List categories
- `POST /api/tags` - Create a tag
- `GET /api/tags` - List tags

### Notifications & Logs
- `GET /api/notifications` - Get your notifications
- `GET /api/activity-logs?entityType=TASK&entityId=...` - View activity history for an entity

*Note: All endpoints (except `/api/auth/**`) require a valid JWT token sent in the `Authorization` header as `Bearer <token>`.*
