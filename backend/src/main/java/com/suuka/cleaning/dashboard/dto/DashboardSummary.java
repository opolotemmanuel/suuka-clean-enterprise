package com.suuka.cleaning.dashboard.dto;

import java.util.List;

public record DashboardSummary(
        String role,
        List<DashboardMetric> metrics,
        List<DashboardAction> quickActions,
        List<String> activity,
        List<String> emptyStates
) {
}
