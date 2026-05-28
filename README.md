# Concurrent Microservices Demo

A Spring Boot 3 + Java 17 demonstration of concurrent communication between three microservices.

## Architecture

- **M1 (Employee Service)**: Main service that calls M3 (non-blocking) and is blocked by M2
- **M2 (Department Service)**: Blocking service that delays M1 execution
- **M3 (Salary Service)**: Non-blocking service called asynchronously by M1

## Features

✅ Spring Boot 3.2.0
✅ Java 17
✅ JPA/Hibernate
✅ OpenFeign for inter-service communication
✅ Async/CompletableFuture for non-blocking calls
✅ H2 in-memory database
✅ RESTful APIs

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+

### Run Services

```bash
# Terminal 1: M2 - Department Service (Port 8082)
cd m2-department-service
mvn spring-boot:run

# Terminal 2: M3 - Salary Service (Port 8083)
cd m3-salary-service
mvn spring-boot:run

# Terminal 3: M1 - Employee Service (Port 8081)
cd m1-employee-service
mvn spring-boot:run
```

## API Endpoints

### Department Service (M2)
```bash
# Create Department
POST http://localhost:8082/api/department
{"name":"Engineering","location":"USA"}

# Get Department (2 second delay)
GET http://localhost:8082/api/department/1
```

### Salary Service (M3)
```bash
# Create Salary
POST http://localhost:8083/api/salary
{"employeeId":1,"amount":100000,"currency":"USD"}

# Get Salary
GET http://localhost:8083/api/salary/1
```

### Employee Service (M1)
```bash
# Create Employee
POST http://localhost:8081/api/employees
{"name":"John Doe","email":"john@example.com","departmentId":1}

# Get Employee
GET http://localhost:8081/api/employees/1

# Get Employee with Details (Non-blocking, handles M2 blocking + M3 async)
GET http://localhost:8081/api/employees/1/details
```

## How Concurrency Works

1. **M1 calls M2 (Blocking)**: Waits synchronously for department info
2. **M1 calls M3 (Non-blocking)**: Async call happens in parallel using CompletableFuture
3. **Result**: Combined response with employee, department, and salary data

## Project Structure

```
concurrent-demo/
├── m1-employee-service/
│   ├── src/
│   │   └── main/java/com/microservices/m1/
│   │       ├── controller/
│   │       ├── service/
│   │       ├── entity/
│   │       ├── repository/
│   │       ├── client/
│   │       ├── config/
│   │       ├── dto/
│   │       └── EmployeeServiceApplication.java
│   ├── src/main/resources/application.yml
│   └── pom.xml
├── m2-department-service/
│   ├── src/
│   │   └── main/java/com/microservices/m2/
│   ├── pom.xml
│   └── ...
├── m3-salary-service/
│   ├── src/
│   │   └── main/java/com/microservices/m3/
│   ├── pom.xml
│   └── ...
└── docker-compose.yml
```

## Testing Flow

1. Create a department in M2
2. Create salary record in M3
3. Create employee in M1
4. Call `/api/employees/1/details` to see concurrent execution

## Key Technologies

- **Spring Boot 3**: Latest framework
- **Java 17**: Modern Java features
- **JPA**: Object-relational mapping
- **OpenFeign**: Declarative REST client
- **CompletableFuture**: Non-blocking async calls
- **H2 Database**: In-memory for testing

## Author
HemAddy
