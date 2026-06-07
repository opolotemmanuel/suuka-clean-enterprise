package com.suuka.cleaning.auth.repository;

import com.suuka.cleaning.auth.entity.CleanerApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CleanerApplicationRepository extends JpaRepository<CleanerApplication, UUID> {
    Optional<CleanerApplication> findByUserId(UUID userId);
}
