package com.JavaCSA.credit_service.repository;

import com.JavaCSA.credit_service.entity.CreditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CreditRepository extends JpaRepository<CreditEntity, Integer> {
    List<CreditEntity> findByUserId(int userId);
    void deleteByUserId(int userId);
    CreditEntity findTopByUserIdOrderByDateDesc(int userId);
    CreditEntity findTopByEmailIdOrderByDateDesc(String emailId);
    List<CreditEntity> findByLastUpdatedBefore(LocalDateTime dateTime);
}
