package com.microservices.m3.service;

import com.microservices.m3.entity.Salary;
import com.microservices.m3.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalaryService {

    private final SalaryRepository salaryRepository;

    /**
     * Non-blocking service - no artificial delays
     * Called asynchronously by M1
     */
    public Salary getSalary(Long employeeId) {
        long startTime = System.currentTimeMillis();
        log.info("[M3] Fetching salary for employee: {}", employeeId);
        
        Salary salary = salaryRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Salary not found for employee: " + employeeId));
        
        long duration = System.currentTimeMillis() - startTime;
        log.info("[M3] Salary fetched in {} ms: {}", duration, salary.getAmount());
        
        return salary;
    }

    public Salary createSalary(Salary salary) {
        log.info("[M3] Creating salary for employee: {}", salary.getEmployeeId());
        return salaryRepository.save(salary);
    }
}
