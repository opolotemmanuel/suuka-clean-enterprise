package com.suuka.cleaning.inventory.entity;

import com.suuka.cleaning.common.entity.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory_items")
public class InventoryItem extends AuditableEntity {
    @Column(nullable = false)
    private String name;
    private String sku;
    private int quantity;
    private int reorderLevel;
    private String unit;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}
