package com.suuka.cleaning.dashboard.controller;

import com.suuka.cleaning.auth.security.SuukaPrincipal;
import com.suuka.cleaning.common.response.ApiResponse;
import com.suuka.cleaning.dashboard.dto.DashboardSummary;
import com.suuka.cleaning.dashboard.service.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<DashboardSummary> summary(@AuthenticationPrincipal SuukaPrincipal principal) {
        return ApiResponse.success("Dashboard summary loaded", dashboardService.summary(principal));
    }
}
