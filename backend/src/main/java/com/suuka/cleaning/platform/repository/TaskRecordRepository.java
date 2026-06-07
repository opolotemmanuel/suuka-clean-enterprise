package com.suuka.cleaning.platform.repository;

import com.suuka.cleaning.platform.entity.TaskRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRecordRepository extends JpaRepository<TaskRecord, UUID> {
    List<TaskRecord> findByAssignedToOrderByCreatedAtDesc(String assignedTo);
}
