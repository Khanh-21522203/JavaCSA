package com.JavaCSA.credit_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationDTO {
    private Long creditScore;
    private String emailId;
}
