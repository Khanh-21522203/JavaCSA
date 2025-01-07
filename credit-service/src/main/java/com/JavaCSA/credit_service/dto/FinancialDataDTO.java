package com.JavaCSA.credit_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FinancialDataDTO {
    private int userId;
    private double creditScore;
    private double transactionAmount;
    private String emailId;
    private String transactionType;
}
