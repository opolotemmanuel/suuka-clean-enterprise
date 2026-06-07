package com.suuka.cleaning.ai.controller;

import com.suuka.cleaning.ai.entity.AIRecommendation;
import com.suuka.cleaning.ai.repository.AIRecommendationRepository;
import com.suuka.cleaning.common.enums.AIRecommendationStatus;
import com.suuka.cleaning.common.response.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/ai/recommendations")
public class AIRecommendationController {
    private final AIRecommendationRepository repository;

    public AIRecommendationController(AIRecommendationRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_AI_INTELLIGENCE')")
    public ApiResponse<List<AIRecommendation>> all() {
        return ApiResponse.success("AI recommendations loaded", repository.findAll());
    }

    @PostMapping("/{id}/status/{status}")
    @PreAuthorize("hasAuthority('VIEW_AI_INTELLIGENCE')")
    public ApiResponse<AIRecommendation> status(@PathVariable UUID id, @PathVariable AIRecommendationStatus status) {
        AIRecommendation recommendation = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("AI recommendation not found"));
        recommendation.setAiStatus(status);
        return ApiResponse.success("AI recommendation status updated", repository.save(recommendation));
    }
}
