package com.suuka.cleaning.inventory.controller;

import com.suuka.cleaning.auth.security.SuukaPrincipal;
import com.suuka.cleaning.common.response.ApiResponse;
import com.suuka.cleaning.inventory.dto.InventoryItemRequest;
import com.suuka.cleaning.inventory.dto.SupplyRequestDto;
import com.suuka.cleaning.inventory.entity.InventoryItem;
import com.suuka.cleaning.inventory.entity.SupplyRequest;
import com.suuka.cleaning.inventory.repository.InventoryItemRepository;
import com.suuka.cleaning.inventory.repository.SupplyRequestRepository;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class InventoryController {
    private final InventoryItemRepository inventoryItemRepository;
    private final SupplyRequestRepository supplyRequestRepository;

    public InventoryController(InventoryItemRepository inventoryItemRepository, SupplyRequestRepository supplyRequestRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.supplyRequestRepository = supplyRequestRepository;
    }

    @GetMapping("/admin/inventory")
    @PreAuthorize("hasAuthority('MANAGE_INVENTORY')")
    public ApiResponse<List<InventoryItem>> items() {
        return ApiResponse.success("Inventory loaded", inventoryItemRepository.findAll());
    }

    @PostMapping("/admin/inventory")
    @PreAuthorize("hasAuthority('MANAGE_INVENTORY')")
    public ApiResponse<InventoryItem> create(@Valid @RequestBody InventoryItemRequest request) {
        InventoryItem item = new InventoryItem();
        item.setName(request.name());
        item.setSku(request.sku());
        item.setQuantity(request.quantity());
        item.setReorderLevel(request.reorderLevel());
        item.setUnit(request.unit());
        return ApiResponse.success("Inventory item saved", inventoryItemRepository.save(item));
    }

    @GetMapping("/admin/inventory/low-stock")
    @PreAuthorize("hasAuthority('MANAGE_INVENTORY')")
    public ApiResponse<List<InventoryItem>> lowStock() {
        return ApiResponse.success("Low stock items loaded", inventoryItemRepository.findAll().stream()
                .filter(item -> item.getQuantity() <= item.getReorderLevel())
                .toList());
    }

    @PostMapping("/cleaner/supply-request")
    @PreAuthorize("hasAuthority('VIEW_ASSIGNED_JOBS')")
    public ApiResponse<SupplyRequest> requestSupply(@AuthenticationPrincipal SuukaPrincipal principal, @Valid @RequestBody SupplyRequestDto request) {
        SupplyRequest supplyRequest = new SupplyRequest();
        supplyRequest.setCleanerId(principal.getId());
        supplyRequest.setInventoryItemId(request.inventoryItemId());
        supplyRequest.setQuantity(request.quantity());
        supplyRequest.setReason(request.reason());
        return ApiResponse.success("Supply request created", supplyRequestRepository.save(supplyRequest));
    }

    @PostMapping("/admin/inventory/supply-requests/{id}/approve")
    @PreAuthorize("hasAuthority('APPROVE_PURCHASES')")
    public ApiResponse<SupplyRequest> approveSupplyRequest(@PathVariable UUID id) {
        SupplyRequest supplyRequest = supplyRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supply request not found"));
        supplyRequest.setApproved(true);
        return ApiResponse.success("Supply request approved", supplyRequestRepository.save(supplyRequest));
    }
}
