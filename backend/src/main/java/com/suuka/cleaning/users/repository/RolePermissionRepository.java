package com.suuka.cleaning.users.repository;

import com.suuka.cleaning.common.enums.Role;
import com.suuka.cleaning.users.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {
    List<RolePermission> findByRole(Role role);
}
