package com.microservices.m1.service;

import com.microservices.m1.client.DepartmentClient;
import com.microservices.m1.client.SalaryClient;
import com.microservices.m1.dto.EmployeeDTO;
import com.microservices.m1.entity.Employee;
import com.microservices.m1.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final SalaryClient salaryClient;
    private final DepartmentClient departmentClient;

    /**
     * Async call to M3 (Salary Service) - Non-blocking
     */
    @Async("taskExecutor")
    public CompletableFuture<Double> fetchSalaryAsync(Long employeeId) {
        long startTime = System.currentTimeMillis();
        log.info("[ASYNC] Fetching salary for employee: {} at {}", employeeId, startTime);
        try {
            var salaryResponse = salaryClient.getSalary(employeeId);
            long duration = System.currentTimeMillis() - startTime;
            log.info("[ASYNC] Salary fetched in {} ms: {}", duration, salaryResponse.getAmount());
            return CompletableFuture.completedFuture(salaryResponse.getAmount());
        } catch (Exception e) {
            log.error("[ASYNC] Error fetching salary for employee: {}", employeeId, e);
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Blocking call to M2 (Department Service) - Synchronous
     */
    public String fetchDepartmentSync(Long departmentId) {
        long startTime = System.currentTimeMillis();
        log.info("[SYNC] Fetching department: {} at {}", departmentId, startTime);
        try {
            var deptResponse = departmentClient.getDepartment(departmentId);
            long duration = System.currentTimeMillis() - startTime;
            log.info("[SYNC] Department fetched in {} ms: {}", duration, deptResponse.getName());
            return deptResponse.getName();
        } catch (Exception e) {
            log.error("[SYNC] Error fetching department: {}", departmentId, e);
            return "Unknown";
        }
    }

    /**
     * Combined approach: Non-blocking for Salary (M3), Blocking for Department (M2)
     * Demonstrates concurrent communication pattern
     */
    public CompletableFuture<EmployeeDTO> getEmployeeWithDetailsAsync(Long employeeId) {
        long totalStartTime = System.currentTimeMillis();
        log.info("===== START: Processing employee {} =====", employeeId);

        return CompletableFuture.supplyAsync(() -> {
            log.info("[THREAD] Current thread: {}", Thread.currentThread().getName());
            
            // Fetch employee from DB
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            // Blocking call to M2 (Department) - This blocks the thread
            String departmentName = fetchDepartmentSync(employee.getDepartmentId());
            
            return new EmployeeDTO(employee.getId(), employee.getName(), 
                    employee.getEmail(), departmentName, null);
        })
        .thenCompose(employeeDTO -> 
            // Non-blocking call to M3 (Salary) - Happens in parallel
            fetchSalaryAsync(employeeId)
                    .thenApply(salary -> {
                        employeeDTO.setSalary(salary);
                        long totalDuration = System.currentTimeMillis() - totalStartTime;
                        log.info("===== END: Completed in {} ms =====", totalDuration);
                        return employeeDTO;
                    })
        );
    }

    public Employee createEmployee(Employee employee) {
        log.info("Creating employee: {}", employee.getName());
        return employeeRepository.save(employee);
    }

    public Employee getEmployee(Long id) {
        log.info("Fetching employee: {}", id);
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }
}
