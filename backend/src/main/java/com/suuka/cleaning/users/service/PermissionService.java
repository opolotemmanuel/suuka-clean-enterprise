package com.suuka.cleaning.users.service;

import com.suuka.cleaning.common.enums.Permission;
import com.suuka.cleaning.common.enums.Role;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Service
public class PermissionService {
    private final Map<Role, Set<Permission>> rolePermissions = new EnumMap<>(Role.class);

    public PermissionService() {
        rolePermissions.put(Role.CLIENT, EnumSet.of(
                Permission.VIEW_OWN_BOOKINGS,
                Permission.CREATE_BOOKING,
                Permission.CANCEL_OWN_BOOKING,
                Permission.REVIEW_BOOKING,
                Permission.USE_CHATBOT
        ));
        rolePermissions.put(Role.CLEANER, EnumSet.of(
                Permission.VIEW_ASSIGNED_JOBS,
                Permission.UPDATE_JOB_STATUS,
                Permission.USE_CHATBOT
        ));
        rolePermissions.put(Role.SUPERVISOR, EnumSet.of(
                Permission.VIEW_ASSIGNED_JOBS,
                Permission.MANAGE_BOOKINGS,
                Permission.MANAGE_QUALITY,
                Permission.MANAGE_COMPLAINTS,
                Permission.USE_CHATBOT
        ));
        rolePermissions.put(Role.CUSTOMER_SUCCESS_MANAGER, EnumSet.of(
                Permission.MANAGE_CLIENTS,
                Permission.MANAGE_CRM,
                Permission.MANAGE_COMPLAINTS,
                Permission.USE_CHATBOT
        ));
        rolePermissions.put(Role.OPERATIONS_MANAGER, EnumSet.of(
                Permission.MANAGE_BOOKINGS,
                Permission.MANAGE_CLEANERS,
                Permission.MANAGE_SERVICE_CATALOG,
                Permission.MANAGE_PRICING,
                Permission.VIEW_REPORTS,
                Permission.USE_CHATBOT
        ));
        rolePermissions.put(Role.HR_MANAGER, EnumSet.of(
                Permission.MANAGE_CLEANERS,
                Permission.MANAGE_WORKFORCE,
                Permission.VIEW_REPORTS,
                Permission.USE_CHATBOT
        ));
        rolePermissions.put(Role.FINANCE_MANAGER, EnumSet.of(
                Permission.VIEW_FINANCE,
                Permission.APPROVE_REFUNDS,
                Permission.REVIEW_APPROVAL_REQUESTS,
                Permission.VIEW_REPORTS,
                Permission.USE_CHATBOT
        ));
        rolePermissions.put(Role.INVENTORY_MANAGER, EnumSet.of(
                Permission.MANAGE_INVENTORY,
                Permission.APPROVE_PURCHASES,
                Permission.MANAGE_SUPPLIERS,
                Permission.REVIEW_APPROVAL_REQUESTS,
                Permission.USE_CHATBOT
        ));
        rolePermissions.put(Role.SYSTEM_ADMIN, EnumSet.of(
                Permission.MANAGE_CLIENTS,
                Permission.MANAGE_CLEANERS,
                Permission.MANAGE_BOOKINGS,
                Permission.MANAGE_SERVICE_CATALOG,
                Permission.MANAGE_PRICING,
                Permission.VIEW_FINANCE,
                Permission.MANAGE_INVENTORY,
                Permission.MANAGE_SUPPLIERS,
                Permission.MANAGE_WORKFORCE,
                Permission.MANAGE_CRM,
                Permission.MANAGE_COMPLAINTS,
                Permission.MANAGE_QUALITY,
                Permission.MANAGE_MARKETING,
                Permission.MANAGE_CORPORATE_ACCOUNTS,
                Permission.MANAGE_TERRITORY,
                Permission.MANAGE_DOCUMENTS,
                Permission.MANAGE_ASSETS,
                Permission.MANAGE_INCIDENTS,
                Permission.MANAGE_NOTIFICATIONS,
                Permission.MANAGE_TASKS,
                Permission.VIEW_BUSINESS_RECORDS,
                Permission.CREATE_APPROVAL_REQUEST,
                Permission.REVIEW_APPROVAL_REQUESTS,
                Permission.VIEW_AI_INTELLIGENCE,
                Permission.MANAGE_AI_SETTINGS,
                Permission.USE_CHATBOT,
                Permission.VIEW_REPORTS,
                Permission.VIEW_AUDIT_LOGS,
                Permission.MANAGE_SYSTEM_SETTINGS
        ));
        rolePermissions.put(Role.EXECUTIVE_ADMIN, EnumSet.allOf(Permission.class));
    }

    public Set<Permission> permissionsFor(Role role) {
        return EnumSet.copyOf(rolePermissions.getOrDefault(role, EnumSet.noneOf(Permission.class)));
    }
}
