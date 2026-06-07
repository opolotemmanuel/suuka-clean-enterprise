package com.suuka.cleaning.approvals.dto;

import com.suuka.cleaning.common.enums.ApprovalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateApprovalRequest(@NotNull ApprovalType approvalType, @NotBlank String title, @NotBlank String reason) {
}
