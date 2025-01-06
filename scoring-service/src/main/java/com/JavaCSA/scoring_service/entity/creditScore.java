package com.JavaCSA.scoring_service.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "credit-scores")
public class creditScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "score")
    private Long score;

    @Column(name = "date")
    private String date;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}
