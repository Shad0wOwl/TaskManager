# TaskManager

A full-stack task management web application built with **Java, Spring Boot, and PostgreSQL**, featuring a responsive user interface, secure authentication, and user-specific task management.

## Status

🚧 **Work in Progress** — Core functionality and automated unit tests are complete. Deployment is the next development milestone.

## Features

- User registration and login
- Automatic login after registration
- Session-based authentication with Spring Security
- Secure password hashing using BCrypt
- Create, view, edit, and delete tasks
- Track task completion through status updates
- User-specific task management with access control
- Persistent data storage with PostgreSQL
- Responsive interface with a dark theme

## Tech Stack

### Backend
- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA / Hibernate
- Maven

### Frontend
- HTML5
- CSS3
- JavaScript (Vanilla JS)

### Database
- PostgreSQL

### Testing
- JUnit 5
- Mockito
- Spring Boot Test

## Testing

The project includes **16 automated tests**, covering authentication, task management, access control, and Spring Boot application context initialization.

**Test coverage includes:**
- User registration and duplicate email handling
- Successful and unsuccessful login attempts
- Task creation and retrieval
- Task status updates
- Task editing and deletion
- Prevention of unauthorized task modifications
- Handling of nonexistent resources
- Spring Boot application context initialization

To run all backend tests, navigate to the `BackEnd` directory and execute:

```bash
./mvnw test
```

On Windows:

```powershell
.\mvnw.cmd test
```

**Note:** The application context test requires a running PostgreSQL database and correctly configured `DB_USERNAME` and `DB_PASSWORD` environment variables.

**Latest test result:** 16 passed, 0 failures, 0 errors.

## Upcoming

- Deployment
- Final documentation improvements