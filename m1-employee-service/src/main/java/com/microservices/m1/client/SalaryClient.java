package com.microservices.m1.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "salary-service", url = "http://localhost:8083")
public interface SalaryClient {
    @GetMapping("/api/salary/{employeeId}")
    SalaryResponse getSalary(@PathVariable Long employeeId);
}
