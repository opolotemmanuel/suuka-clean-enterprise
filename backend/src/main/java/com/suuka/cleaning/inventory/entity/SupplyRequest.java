package com.suuka.cleaning.inventory.entity;

import com.suuka.cleaning.common.entity.AuditableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "supply_requests")
public class SupplyRequest extends AuditableEntity {
    private UUID cleanerId;
    private UUID inventoryItemId;
    private int quantity;
    private String reason;
    private boolean approved;

    public UUID getCleanerId() { return cleanerId; }
    public void setCleanerId(UUID cleanerId) { this.cleanerId = cleanerId; }
    public UUID getInventoryItemId() { return inventoryItemId; }
    public void setInventoryItemId(UUID inventoryItemId) { this.inventoryItemId = inventoryItemId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
}
