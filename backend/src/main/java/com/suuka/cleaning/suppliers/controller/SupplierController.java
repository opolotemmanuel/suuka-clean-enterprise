package com.suuka.cleaning.suppliers.controller;

import com.suuka.cleaning.common.response.ApiResponse;
import com.suuka.cleaning.suppliers.entity.PurchaseOrder;
import com.suuka.cleaning.suppliers.entity.Supplier;
import com.suuka.cleaning.suppliers.repository.PurchaseOrderRepository;
import com.suuka.cleaning.suppliers.repository.SupplierRepository;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class SupplierController {
    private final SupplierRepository supplierRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    public SupplierController(SupplierRepository supplierRepository, PurchaseOrderRepository purchaseOrderRepository) {
        this.supplierRepository = supplierRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    @GetMapping("/suppliers")
    @PreAuthorize("hasAuthority('MANAGE_SUPPLIERS')")
    public ApiResponse<List<Supplier>> suppliers() {
        return ApiResponse.success("Suppliers loaded", supplierRepository.findAll());
    }

    @PostMapping("/suppliers")
    @PreAuthorize("hasAuthority('MANAGE_SUPPLIERS')")
    public ApiResponse<Supplier> saveSupplier(@Valid @RequestBody Supplier supplier) {
        return ApiResponse.success("Supplier saved", supplierRepository.save(supplier));
    }

    @GetMapping("/purchase-orders")
    @PreAuthorize("hasAuthority('MANAGE_SUPPLIERS')")
    public ApiResponse<List<PurchaseOrder>> purchaseOrders() {
        return ApiResponse.success("Purchase orders loaded", purchaseOrderRepository.findAll());
    }

    @PostMapping("/purchase-orders/{id}/approve")
    @PreAuthorize("hasAuthority('APPROVE_PURCHASES')")
    public ApiResponse<PurchaseOrder> approvePurchaseOrder(@PathVariable UUID id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Purchase order not found"));
        purchaseOrder.setApproved(true);
        return ApiResponse.success("Purchase order approved", purchaseOrderRepository.save(purchaseOrder));
    }
}
