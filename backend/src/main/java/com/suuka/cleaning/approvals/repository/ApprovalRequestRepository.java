package com.suuka.cleaning.approvals.repository;

import com.suuka.cleaning.approvals.entity.ApprovalRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, UUID> {
}
