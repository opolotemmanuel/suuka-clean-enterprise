package com.suuka.cleaning.platform.repository;

import com.suuka.cleaning.platform.entity.BusinessRecord;
import com.suuka.cleaning.platform.enums.PlatformModule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BusinessRecordRepository extends JpaRepository<BusinessRecord, UUID> {
    List<BusinessRecord> findByModuleOrderByCreatedAtDesc(PlatformModule module);
}
