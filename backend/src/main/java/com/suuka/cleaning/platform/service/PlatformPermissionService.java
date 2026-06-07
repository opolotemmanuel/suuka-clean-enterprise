package com.suuka.cleaning.platform.service;

import com.suuka.cleaning.common.enums.Permission;
import com.suuka.cleaning.platform.enums.PlatformModule;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

@Service
public class PlatformPermissionService {
    private final Map<PlatformModule, Permission> writePermissions = new EnumMap<>(PlatformModule.class);

    public PlatformPermissionService() {
        writePermissions.put(PlatformModule.SERVICE_CATALOG, Permission.MANAGE_SERVICE_CATALOG);
        writePermissions.put(PlatformModule.PRICING, Permission.MANAGE_PRICING);
        writePermissions.put(PlatformModule.CLIENTS, Permission.MANAGE_CLIENTS);
        writePermissions.put(PlatformModule.CLEANERS, Permission.MANAGE_CLEANERS);
        writePermissions.put(PlatformModule.INVOICES, Permission.VIEW_FINANCE);
        writePermissions.put(PlatformModule.PAYMENTS, Permission.VIEW_FINANCE);
        writePermissions.put(PlatformModule.WALLET, Permission.VIEW_FINANCE);
        writePermissions.put(PlatformModule.REVIEWS, Permission.REVIEW_BOOKING);
        writePermissions.put(PlatformModule.COMPLAINTS, Permission.MANAGE_COMPLAINTS);
        writePermissions.put(PlatformModule.QUALITY, Permission.MANAGE_QUALITY);
        writePermissions.put(PlatformModule.WORKFORCE, Permission.MANAGE_WORKFORCE);
        writePermissions.put(PlatformModule.CRM, Permission.MANAGE_CRM);
        writePermissions.put(PlatformModule.MARKETING, Permission.MANAGE_MARKETING);
        writePermissions.put(PlatformModule.CORPORATE, Permission.MANAGE_CORPORATE_ACCOUNTS);
        writePermissions.put(PlatformModule.REPORTS, Permission.VIEW_REPORTS);
        writePermissions.put(PlatformModule.BUSINESS_INTELLIGENCE, Permission.VIEW_EXECUTIVE_BI);
        writePermissions.put(PlatformModule.DOCUMENTS, Permission.MANAGE_DOCUMENTS);
        writePermissions.put(PlatformModule.TERRITORY, Permission.MANAGE_TERRITORY);
        writePermissions.put(PlatformModule.INCIDENTS, Permission.MANAGE_INCIDENTS);
        writePermissions.put(PlatformModule.ASSETS, Permission.MANAGE_ASSETS);
        writePermissions.put(PlatformModule.VEHICLES, Permission.MANAGE_ASSETS);
        writePermissions.put(PlatformModule.NOTIFICATIONS, Permission.MANAGE_NOTIFICATIONS);
        writePermissions.put(PlatformModule.TASKS, Permission.MANAGE_TASKS);
        writePermissions.put(PlatformModule.ACTIVITY_TIMELINE, Permission.VIEW_AUDIT_LOGS);
    }

    public void requireRead(PlatformModule module, Collection<? extends GrantedAuthority> authorities) {
        Permission writePermission = writePermissions.get(module);
        if (has(authorities, Permission.VIEW_BUSINESS_RECORDS) || has(authorities, writePermission)) {
            return;
        }
        throw new AccessDeniedException("You do not have permission to access this module");
    }

    public void requireWrite(PlatformModule module, Collection<? extends GrantedAuthority> authorities) {
        Permission writePermission = writePermissions.get(module);
        if (has(authorities, writePermission) || has(authorities, Permission.MANAGE_SYSTEM_SETTINGS)) {
            return;
        }
        throw new AccessDeniedException("You do not have permission to update this module");
    }

    private boolean has(Collection<? extends GrantedAuthority> authorities, Permission permission) {
        return permission != null && authorities.stream().anyMatch(authority -> permission.name().equals(authority.getAuthority()));
    }
}
