package com.suuka.cleaning.approvals.controller;

import com.suuka.cleaning.approvals.dto.CreateApprovalRequest;
import com.suuka.cleaning.approvals.dto.ReviewApprovalRequest;
import com.suuka.cleaning.approvals.entity.ApprovalRequest;
import com.suuka.cleaning.approvals.service.ApprovalService;
import com.suuka.cleaning.auth.security.SuukaPrincipal;
import com.suuka.cleaning.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/approvals")
public class ApprovalController {
    private final ApprovalService approvalService;

    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_APPROVAL_REQUEST')")
    public ApiResponse<ApprovalRequest> create(@AuthenticationPrincipal SuukaPrincipal principal, @Valid @RequestBody CreateApprovalRequest request) {
        return ApiResponse.success("Approval request created", approvalService.create(principal.getId().toString(), request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('REVIEW_APPROVAL_REQUESTS')")
    public ApiResponse<List<ApprovalRequest>> all() {
        return ApiResponse.success("Approval requests loaded", approvalService.all());
    }

    @PostMapping("/{id}/review")
    @PreAuthorize("hasAuthority('REVIEW_APPROVAL_REQUESTS')")
    public ApiResponse<ApprovalRequest> review(
            @AuthenticationPrincipal SuukaPrincipal principal,
            @PathVariable UUID id,
            @Valid @RequestBody ReviewApprovalRequest request
    ) {
        return ApiResponse.success("Approval reviewed", approvalService.review(principal.getId().toString(), id, request.status()));
    }
}
