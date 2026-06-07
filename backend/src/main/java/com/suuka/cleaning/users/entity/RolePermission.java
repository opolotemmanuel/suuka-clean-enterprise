package com.suuka.cleaning.users.entity;

import com.suuka.cleaning.common.entity.AuditableEntity;
import com.suuka.cleaning.common.enums.Permission;
import com.suuka.cleaning.common.enums.Role;
import jakarta.persistence.*;

@Entity
@Table(name = "role_permissions", uniqueConstraints = @UniqueConstraint(columnNames = {"role", "permission"}))
public class RolePermission extends AuditableEntity {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Permission permission;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }
}
