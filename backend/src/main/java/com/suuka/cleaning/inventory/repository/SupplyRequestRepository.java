package com.suuka.cleaning.inventory.repository;

import com.suuka.cleaning.inventory.entity.SupplyRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SupplyRequestRepository extends JpaRepository<SupplyRequest, UUID> {
    List<SupplyRequest> findByCleanerId(UUID cleanerId);
}
