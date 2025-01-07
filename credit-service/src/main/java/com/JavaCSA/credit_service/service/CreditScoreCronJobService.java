package com.JavaCSA.credit_service.service;

import com.JavaCSA.credit_service.dto.CreditScoreDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreditScoreCronJobService {
    @Autowired
    private CreditScoreService creditScoreService;

    /**
     * Scheduled method to process pending credit scores on an hourly basis.
     * It fetches credit scores that have been updated more than 24 hours ago and processes them.
     */
    @Scheduled(cron = "0 0 * * * *") // Cron expression for hourly execution: runs at the top of every hour.
    public void processPendingClaims() {
        // Fetch a list of credit scores that need to be updated or processed.
        List<CreditScoreDTO> creditScores = creditScoreService.fetchScoresUpdatedMoreThan24HoursAgo();
        for (CreditScoreDTO creditScore : creditScores) {
            try {
                // Attempt to process each credit score, here simplified to retrieving a score by user ID.
                creditScoreService.getCreditScoreByEmailId(creditScore.getUserId());
            } catch (Exception e) {
                // Log or handle the exception if processing fails.
                System.out.println("Error processing Credit ID: " + creditScore.getUserId() + " with error: " + e.getMessage());
            }
        }
    }
}
