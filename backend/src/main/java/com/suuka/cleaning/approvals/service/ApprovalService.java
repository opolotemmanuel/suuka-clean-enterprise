package com.suuka.cleaning.approvals.service;

import com.suuka.cleaning.approvals.dto.CreateApprovalRequest;
import com.suuka.cleaning.approvals.entity.ApprovalRequest;
import com.suuka.cleaning.approvals.repository.ApprovalRequestRepository;
import com.suuka.cleaning.audit.service.AuditService;
import com.suuka.cleaning.common.enums.ApprovalStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ApprovalService {
    private final ApprovalRequestRepository repository;
    private final AuditService auditService;

    public ApprovalService(ApprovalRequestRepository repository, AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    @Transactional
    public ApprovalRequest create(String actorId, CreateApprovalRequest request) {
        ApprovalRequest approval = new ApprovalRequest();
        approval.setApprovalType(request.approvalType());
        approval.setTitle(request.title());
        approval.setReason(request.reason());
        approval.setRequestedBy(actorId);
        auditService.record(actorId, "approvals", "REQUEST_CREATED", request.approvalType().name());
        return repository.save(approval);
    }

    public List<ApprovalRequest> all() {
        return repository.findAll();
    }

    @Transactional
    public ApprovalRequest review(String actorId, UUID id, ApprovalStatus status) {
        if (status == ApprovalStatus.PENDING) {
            throw new IllegalArgumentException("Review status must change the approval request");
        }
        ApprovalRequest approval = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Approval request not found"));
        approval.setApprovalStatus(status);
        approval.setReviewedBy(actorId);
        auditService.record(actorId, "approvals", "REQUEST_" + status.name(), approval.getApprovalType().name());
        return approval;
    }
}
