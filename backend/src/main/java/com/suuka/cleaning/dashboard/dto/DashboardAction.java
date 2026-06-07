package com.suuka.cleaning.dashboard.dto;

public record DashboardAction(String label, String actionType, String targetModule, String targetRoute, String requiredPermission) {
}
