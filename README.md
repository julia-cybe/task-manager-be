# Task Manager Backend

A Spring Boot RESTful API for managing tasks, including creation, updating, deletion, and retrieval. This project demonstrates best practices in backend development using Java, Spring Boot, and Maven.
The repository has been build with the help of Claude Code and Github Copilot in agent mode.

## Features
- CRUD operations for tasks
- Validation using DTOs
- Service and repository layers
- Lombok for boilerplate reduction
- JPA/Hibernate for persistence
- Unit and integration tests

## Technologies
- Java 17+
- Spring Boot
- Maven
- JPA/Hibernate
- Lombok
- H2 (default, can be changed)

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven

### Setup
1. Clone the repository:
   ```bash
   git clone <your-repo-url>
   cd task-manager
   ```
2. Build the project:
   ```bash
   ./mvnw clean install
   ```
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```
4. Access the API at `http://localhost:8080`

## API Endpoints

| Method | Endpoint         | Description           |
|--------|------------------|-----------------------|
| GET    | /tasks           | List all tasks        |
| GET    | /tasks/{id}      | Get task by ID        |
| POST   | /tasks           | Create a new task     |
| PUT    | /tasks/{id}      | Update a task         |
| DELETE | /tasks/{id}      | Delete a task         |

Request and response bodies use JSON. See `TaskDTO.java` for field details.

## Testing
Run all tests:
```bash
./mvnw test
```

## Configuration
Edit `src/main/resources/application.properties` for database and other settings.

## Contributing
1. Fork the repository
2. Create your feature branch (`git checkout -b feature/fooBar`)
3. Commit your changes (`git commit -am 'Add some fooBar'`)
4. Push to the branch (`git push origin feature/fooBar`)
5. Create a new Pull Request

## License
This project is licensed under the MIT License.

