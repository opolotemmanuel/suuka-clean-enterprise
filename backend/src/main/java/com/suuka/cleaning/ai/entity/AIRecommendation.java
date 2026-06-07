package com.suuka.cleaning.ai.entity;

import com.suuka.cleaning.common.entity.AuditableEntity;
import com.suuka.cleaning.common.enums.AIRecommendationStatus;
import com.suuka.cleaning.common.enums.Role;
import jakarta.persistence.*;

@Entity
@Table(name = "ai_recommendations")
public class AIRecommendation extends AuditableEntity {
    private String module;
    private String recommendationTitle;

    @Column(length = 4000)
    private String recommendationBody;

    private String priority;
    private double confidenceScore;
    private String riskLevel;

    @Enumerated(EnumType.STRING)
    private Role generatedForRole;

    private String generatedForUser;
    private String approvedBy;

    @Enumerated(EnumType.STRING)
    private AIRecommendationStatus aiStatus = AIRecommendationStatus.NEW;

    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }
    public String getRecommendationTitle() { return recommendationTitle; }
    public void setRecommendationTitle(String recommendationTitle) { this.recommendationTitle = recommendationTitle; }
    public String getRecommendationBody() { return recommendationBody; }
    public void setRecommendationBody(String recommendationBody) { this.recommendationBody = recommendationBody; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    public Role getGeneratedForRole() { return generatedForRole; }
    public void setGeneratedForRole(Role generatedForRole) { this.generatedForRole = generatedForRole; }
    public String getGeneratedForUser() { return generatedForUser; }
    public void setGeneratedForUser(String generatedForUser) { this.generatedForUser = generatedForUser; }
    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    public AIRecommendationStatus getAiStatus() { return aiStatus; }
    public void setAiStatus(AIRecommendationStatus aiStatus) { this.aiStatus = aiStatus; }
}
