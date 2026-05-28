package com.microservices.m3.controller;

import com.microservices.m3.entity.Salary;
import com.microservices.m3.service.SalaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/salary")
@RequiredArgsConstructor
@Slf4j
public class SalaryController {

    private final SalaryService salaryService;

    @PostMapping
    public ResponseEntity<Salary> createSalary(@RequestBody Salary salary) {
        log.info("POST /api/salary - Creating salary");
        return ResponseEntity.ok(salaryService.createSalary(salary));
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<Salary> getSalary(@PathVariable Long employeeId) {
        log.info("GET /api/salary/{}", employeeId);
        return ResponseEntity.ok(salaryService.getSalary(employeeId));
    }
}
