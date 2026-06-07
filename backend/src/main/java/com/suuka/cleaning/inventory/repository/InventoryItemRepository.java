package com.suuka.cleaning.inventory.repository;

import com.suuka.cleaning.inventory.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {
    List<InventoryItem> findByQuantityLessThanEqual(int quantity);
}
