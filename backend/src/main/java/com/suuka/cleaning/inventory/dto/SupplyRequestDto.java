package com.suuka.cleaning.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SupplyRequestDto(@NotNull UUID inventoryItemId, @Min(1) int quantity, String reason) {
}
