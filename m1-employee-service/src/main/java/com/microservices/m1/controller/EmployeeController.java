package com.microservices.m1.controller;

import com.microservices.m1.dto.EmployeeDTO;
import com.microservices.m1.entity.Employee;
import com.microservices.m1.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        log.info("POST /api/employees - Creating employee");
        return ResponseEntity.ok(employeeService.createEmployee(employee));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployee(@PathVariable Long id) {
        log.info("GET /api/employees/{}", id);
        return ResponseEntity.ok(employeeService.getEmployee(id));
    }

    /**
     * Non-blocking endpoint that returns CompletableFuture
     * M1 -> M2 (Blocking, 2 sec delay) + M1 -> M3 (Async)
     */
    @GetMapping("/{id}/details")
    public CompletableFuture<ResponseEntity<EmployeeDTO>> getEmployeeDetails(@PathVariable Long id) {
        log.info("GET /api/employees/{}/details - Concurrent call", id);
        return employeeService.getEmployeeWithDetailsAsync(id)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    log.error("Error fetching employee details", ex);
                    return ResponseEntity.status(500).build();
                });
    }
}
