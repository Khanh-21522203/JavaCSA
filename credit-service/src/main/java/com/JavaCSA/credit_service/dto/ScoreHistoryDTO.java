package com.JavaCSA.credit_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ScoreHistoryDTO {
    private int userId;
    private List<CreditScoreDetails> scores;

    @Data
    @Builder
    public static class CreditScoreDetails {
        private Long score;
        private String date;
    }
}
