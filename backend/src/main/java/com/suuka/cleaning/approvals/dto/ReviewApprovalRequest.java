package com.suuka.cleaning.approvals.dto;

import com.suuka.cleaning.common.enums.ApprovalStatus;
import jakarta.validation.constraints.NotNull;

public record ReviewApprovalRequest(@NotNull ApprovalStatus status) {
}
