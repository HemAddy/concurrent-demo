package com.microservices.m1.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "department-service", url = "http://localhost:8082")
public interface DepartmentClient {
    @GetMapping("/api/department/{departmentId}")
    DepartmentResponse getDepartment(@PathVariable Long departmentId);
}
