package com.microservices.m1.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryResponse {
    private Long employeeId;
    private Double amount;
    private String currency;
}
