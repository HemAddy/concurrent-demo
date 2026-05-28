# Testing Guide - Concurrent Microservices Demo

## Setup & Prerequisites

1. **Java 17+** installed
2. **Maven 3.8+** installed
3. All three services running on ports 8081, 8082, 8083

## Test Scenario

This demonstrates:
- **M1 → M2**: Synchronous/Blocking call (2 second delay)
- **M1 → M3**: Asynchronous/Non-blocking call (parallel execution)

## Step-by-Step Testing

### 1. Start All Services

```bash
# Terminal 1: Department Service (M2)
cd m2-department-service
mvn spring-boot:run

# Terminal 2: Salary Service (M3)
cd m3-salary-service
mvn spring-boot:run

# Terminal 3: Employee Service (M1)
cd m1-employee-service
mvn spring-boot:run
```

### 2. Create Test Data

#### 2.1 Create Department (M2)

```bash
curl -X POST http://localhost:8082/api/department \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Engineering",
    "location": "USA"
  }'
```

Expected Response:
```json
{
  "id": 1,
  "name": "Engineering",
  "location": "USA"
}
```

#### 2.2 Create Salary (M3)

```bash
curl -X POST http://localhost:8083/api/salary \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": 1,
    "amount": 100000,
    "currency": "USD"
  }'
```

Expected Response:
```json
{
  "id": 1,
  "employeeId": 1,
  "amount": 100000,
  "currency": "USD"
}
```

#### 2.3 Create Employee (M1)

```bash
curl -X POST http://localhost:8081/api/employees \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john.doe@example.com",
    "departmentId": 1
  }'
```

Expected Response:
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "departmentId": 1
}
```

### 3. Test Concurrent Communication

#### 3.1 Non-blocking Endpoint with Concurrent Calls

```bash
curl -X GET http://localhost:8081/api/employees/1/details
```

**Timeline of Execution:**
- **T=0ms**: Request starts
- **T=0-2000ms**: M1 makes BLOCKING call to M2 (Department) - 2 second delay
- **T=0-100ms**: M1 makes ASYNC call to M3 (Salary) - happens in parallel
- **T=2000ms**: Both calls complete, combined response returned

Expected Response (after ~2 seconds):
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "departmentName": "Engineering",
  "salary": 100000.0
}
```

#### 3.2 Check Console Logs

M1 Console Output:
```
===== START: Processing employee 1 =====
[THREAD] Current thread: async-1
[SYNC] Fetching department: 1 at 1704067200000
[ASYNC] Fetching salary for employee: 1 at 1704067200005
[SYNC] Department fetched in 2001 ms: Engineering
[ASYNC] Salary fetched in 45 ms: 100000.0
===== END: Completed in 2046 ms =====
```

### 4. Performance Comparison

#### Synchronous Call (if both were blocking)
```
Time = 2000ms (M2 blocking) + 45ms (M3) = ~2045ms
```

#### With Concurrent Calls (Current Implementation)
```
Time = max(2000ms for M2, 45ms for M3) = ~2045ms
       (M3 runs in parallel, so doesn't add extra time)
```

**Benefit**: M3 call completes while M2 is still processing, saving time!

### 5. Additional Tests

#### 5.1 Get Simple Employee Data

```bash
curl -X GET http://localhost:8081/api/employees/1
```

This returns only database data (no external calls).

#### 5.2 Get Department with Blocking

```bash
curl -X GET http://localhost:8082/api/department/1
```

This will take ~2 seconds (blocking operation).

#### 5.3 Get Salary (Fast)

```bash
curl -X GET http://localhost:8083/api/salary/1
```

This returns immediately (no artificial delay).

## Error Scenarios

### Test 1: Invalid Employee ID
```bash
curl -X GET http://localhost:8081/api/employees/999/details
```

Expected: 500 error (Employee not found)

### Test 2: Invalid Department ID
```bash
curl -X GET http://localhost:8082/api/department/999
```

Expected: 500 error (Department not found)

### Test 3: Invalid Salary Employee ID
```bash
curl -X GET http://localhost:8083/api/salary/999
```

Expected: 500 error (Salary not found)

## H2 Database Console

You can access H2 console for each service:

- **M1**: http://localhost:8081/h2-console
- **M2**: http://localhost:8082/h2-console
- **M3**: http://localhost:8083/h2-console

### M1 Employees Table Query
```sql
SELECT * FROM employees;
```

### M2 Departments Table Query
```sql
SELECT * FROM departments;
```

### M3 Salaries Table Query
```sql
SELECT * FROM salaries;
```

## Docker Testing

### Build and Run with Docker Compose

```bash
docker-compose up --build
```

### Services will be available at:
- M1 (Employee): http://localhost:8081
- M2 (Department): http://localhost:8082
- M3 (Salary): http://localhost:8083

### Stop Services

```bash
docker-compose down
```

## Key Learning Points

1. **Blocking Calls**: M1 → M2 is synchronous (2 sec wait)
2. **Async Calls**: M1 → M3 happens in parallel using CompletableFuture
3. **Thread Pool**: Configured in AsyncConfig (5 core, 10 max threads)
4. **Total Time**: Limited by longest operation (~2 seconds for M2)
5. **Scalability**: Adding more M3 calls wouldn't increase total time much

## Monitoring

Watch the console logs for:
- `[SYNC]` tags for blocking operations
- `[ASYNC]` tags for non-blocking operations
- Execution times for each operation
- Total end-to-end time
