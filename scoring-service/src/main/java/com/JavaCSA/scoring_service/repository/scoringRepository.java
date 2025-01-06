package com.JavaCSA.scoring_service.repository;

import com.JavaCSA.scoring_service.entity.creditScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface scoringRepository extends JpaRepository<creditScore, Integer> {
    List<creditScore> findByUserId(int userId);
    void deleteByUserId(int userId);
    creditScore findTopByUserIdOrderByDateDesc(int userId);
    creditScore findTopByEmailIdOrderByDateDesc(String emailId);
    List<creditScore> findByLastUpdatedBefore(LocalDateTime dateTime);
}
