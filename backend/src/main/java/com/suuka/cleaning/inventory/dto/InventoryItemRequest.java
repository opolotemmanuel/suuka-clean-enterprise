package com.suuka.cleaning.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record InventoryItemRequest(@NotBlank String name, String sku, @Min(0) int quantity, @Min(0) int reorderLevel, String unit) {
}
