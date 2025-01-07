package com.JavaCSA.credit_service.repository;

import com.JavaCSA.credit_service.entity.CreditScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CreditRepository extends JpaRepository<CreditScore, Integer> {
    List<CreditScore> findByUserId(int userId);
    void deleteByUserId(int userId);
    CreditScore findTopByUserIdOrderByDateDesc(int userId);
    CreditScore findTopByEmailIdOrderByDateDesc(String emailId);
    List<CreditScore> findByLastUpdatedBefore(LocalDateTime dateTime);
}
