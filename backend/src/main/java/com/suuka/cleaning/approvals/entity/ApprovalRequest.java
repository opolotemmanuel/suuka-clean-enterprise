package com.suuka.cleaning.approvals.entity;

import com.suuka.cleaning.common.entity.AuditableEntity;
import com.suuka.cleaning.common.enums.ApprovalStatus;
import com.suuka.cleaning.common.enums.ApprovalType;
import jakarta.persistence.*;

@Entity
@Table(name = "approval_requests")
public class ApprovalRequest extends AuditableEntity {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalType approvalType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @Column(nullable = false)
    private String title;

    @Column(length = 4000)
    private String reason;

    private String requestedBy;
    private String reviewedBy;

    public ApprovalType getApprovalType() {
        return approvalType;
    }

    public void setApprovalType(ApprovalType approvalType) {
        this.approvalType = approvalType;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }
}
