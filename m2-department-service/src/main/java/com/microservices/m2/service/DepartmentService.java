package com.microservices.m2.service;

import com.microservices.m2.entity.Department;
import com.microservices.m2.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    /**
     * Blocking operation - simulates a 2 second delay
     * This blocks M1 when called synchronously
     */
    public Department getDepartment(Long id) {
        log.info("[M2] Fetching department: {} - Starting blocking operation", id);
        long startTime = System.currentTimeMillis();
        
        try {
            // Simulate blocking I/O operation (2 second delay)
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("[M2] Thread interrupted", e);
        }
        
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        
        long duration = System.currentTimeMillis() - startTime;
        log.info("[M2] Department fetched in {} ms: {}", duration, department.getName());
        
        return department;
    }

    public Department createDepartment(Department department) {
        log.info("[M2] Creating department: {}", department.getName());
        return departmentRepository.save(department);
    }
}
