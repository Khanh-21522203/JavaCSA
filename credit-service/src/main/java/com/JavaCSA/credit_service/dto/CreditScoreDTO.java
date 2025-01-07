package com.JavaCSA.credit_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreditScoreDTO {
    private int userId;
    private Long score;
    private String date;
}
