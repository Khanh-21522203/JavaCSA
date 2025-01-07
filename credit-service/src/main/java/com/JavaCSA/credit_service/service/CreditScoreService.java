package com.JavaCSA.credit_service.service;

import com.JavaCSA.credit_service.client.UserManagementClient;
import com.JavaCSA.credit_service.dto.CreditScoreDTO;
import com.JavaCSA.credit_service.dto.NotificationDTO;
import com.JavaCSA.credit_service.dto.ScoreHistoryDTO;
import com.JavaCSA.credit_service.entity.CreditScore;
import com.JavaCSA.credit_service.repository.CreditRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CreditScoreService {

    @Autowired
    private CreditRepository creditRepository;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private static final Logger logger = LogManager.getLogger(CreditScoreService.class);
    private static final String CREDIT_SCORE_UPDATE = "credit-score-updates";
    @Autowired
    private UserManagementClient userManagementClient;

    /**
     * Retrieves the latest credit score by user ID from Redis cache or database, then sends an update to Kafka.
     */
    public CreditScoreDTO getCreditScoreByEmailId(int userId) {
        String emailId = userManagementClient.getUserDetails(userId).block();  // Synchronous call to get user details.
        CreditScore creditScore = (CreditScore) redisTemplate.opsForValue().get(emailId);
        if (creditScore == null) {
            logger.info("Fetching from DB");
            creditScore = creditRepository.findTopByEmailIdOrderByDateDesc(emailId);
        } else {
            logger.info("Fetched from Redis");
        }
        sendToKafka(creditScore.getScore(), emailId);
        return convertToDTO(creditScore);
    }

    /**
     * Retrieves the latest credit score for a user by ID from the database.
     */
    public CreditScoreDTO getCreditScoreByUserId(int userId) {
        CreditScore creditScore = creditRepository.findTopByUserIdOrderByDateDesc(userId);
        return convertToDTO(creditScore);
    }

    /**
     * Calculates a new credit score, saves it to the database and cache.
     */
    public CreditScoreDTO calculateCreditScore(CreditScoreDTO creditScoreDTO) {
        String emailId = userManagementClient.getUserDetails(creditScoreDTO.getUserId()).block();
        redisTemplate.opsForValue().set(emailId, convertToEntity(creditScoreDTO));
        CreditScore creditScore = creditRepository.save(convertToEntity(creditScoreDTO));
        return convertToDTO(creditScore);
    }

    /**
     * Updates an existing credit score in the database.
     */
    public CreditScoreDTO updateCreditScore(int userId, CreditScoreDTO creditScoreDTO) {
        CreditScore existingScore = creditRepository.findTopByUserIdOrderByDateDesc(userId);
        if (existingScore == null) {
            throw new IllegalArgumentException("Credit score not found");
        }
        existingScore.setScore(creditScoreDTO.getScore());
        existingScore.setDate(creditScoreDTO.getDate());
        existingScore = creditRepository.save(existingScore);
        return convertToDTO(existingScore);
    }

        /**
         * Sends credit score updates to a Kafka topic.
         */
    private void sendToKafka(Long score, String emailId) {
        NotificationDTO message = NotificationDTO.builder()
                .creditScore(score)
                .emailId(emailId)
                .build();
        kafkaTemplate.send(CREDIT_SCORE_UPDATE, message);
    }
    /**
     * Deletes a credit score by user ID.
     */
    public void deleteCreditScoreByUserId(int userId) {
        creditRepository.deleteByUserId(userId);
    }

    /**
     * Retrieves the credit score history for a user, transforming each score into a detailed DTO.
     */
    public List<ScoreHistoryDTO> getCreditScoreHistoryByUserId(int userId) {
        List<CreditScore> scoreHistory = creditRepository.findByUserId(userId);
        return scoreHistory.stream().map(this::convertToScoreHistoryDTO).collect(Collectors.toList());
    }

    /**
     * Handles batch processing of credit scores, useful for bulk operations.
     */
    public List<CreditScoreDTO> calculateBatchCreditScores(List<CreditScoreDTO> creditScoresDTOs) {
        List<CreditScore> scores = creditScoresDTOs.stream().map(this::convertToEntity).collect(Collectors.toList());
        creditRepository.saveAll(scores);
        return scores.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Calculates the average credit score of all entries in the database.
     */
    public double getAverageCreditScore() {
        return creditRepository.findAll().stream().mapToLong(CreditScore::getScore).average().orElse(Double.NaN);
    }

    /**
     * Refreshes all credit scores, potentially recalculating or updating them.
     */
    public void refreshCreditScores() {
        List<CreditScore> scores = creditRepository.findAll();
        scores.forEach(score -> score.setScore(score.getScore() + 1));  // Example update logic.
        creditRepository.saveAll(scores);
    }

    /**
     * Converts a CreditScore entity to a CreditScoreDTO.
     */
    private CreditScoreDTO convertToDTO(CreditScore creditScore) {
        return CreditScoreDTO.builder()
                .userId(creditScore.getUserId())
                .score(creditScore.getScore())
                .date(creditScore.getDate())
                .build();
    }

    /**
     * Converts a CreditScoreDTO to a CreditScore entity.
     */
    private CreditScore convertToEntity(CreditScoreDTO creditScoreDTO) {
        return  CreditScore.builder()
                .userId(creditScoreDTO.getUserId())
                .score(creditScoreDTO.getScore())
                .date(creditScoreDTO.getDate())
                .build();
    }

    /**
     * Converts a CreditScore entity into a ScoreHistoryDTO.
     */
    private ScoreHistoryDTO convertToScoreHistoryDTO(CreditScore creditScore) {
        ScoreHistoryDTO.CreditScoreDetails details = ScoreHistoryDTO.CreditScoreDetails.builder()
                .score(creditScore.getScore())
                .date(creditScore.getDate())
                .build();

        return ScoreHistoryDTO.builder()
                .userId(creditScore.getUserId())
                .scores(List.of(details))
                .build();
    }

    /**
     * Fetches credit scores that were last updated more than 24 hours ago.
     */
    public List<CreditScoreDTO> fetchScoresUpdatedMoreThan24HoursAgo() {
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        List<CreditScore> scores = creditRepository.findByLastUpdatedBefore(twentyFourHoursAgo);
        return scores.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
}
