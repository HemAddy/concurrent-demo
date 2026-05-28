package com.microservices.m3.repository;

import com.microservices.m3.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    Salary findByEmployeeId(Long employeeId);
}
