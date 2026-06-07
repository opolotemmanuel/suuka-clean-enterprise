package com.suuka.cleaning.ai.repository;

import com.suuka.cleaning.ai.entity.AIRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AIRecommendationRepository extends JpaRepository<AIRecommendation, UUID> {
}
