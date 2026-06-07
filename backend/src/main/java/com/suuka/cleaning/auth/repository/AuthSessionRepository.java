package com.suuka.cleaning.auth.repository;

import com.suuka.cleaning.auth.entity.AuthSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthSessionRepository extends JpaRepository<AuthSession, UUID> {
    List<AuthSession> findByUserIdOrderByCreatedAtDesc(UUID userId);
    Optional<AuthSession> findByRefreshTokenHash(String refreshTokenHash);
}
