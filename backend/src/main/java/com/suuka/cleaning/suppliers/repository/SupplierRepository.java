package com.suuka.cleaning.suppliers.repository;

import com.suuka.cleaning.suppliers.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SupplierRepository extends JpaRepository<Supplier, UUID> {
}
