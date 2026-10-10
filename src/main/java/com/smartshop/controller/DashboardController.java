package com.smartshop.controller;

import com.smartshop.dto.DashboardSummary;
import com.smartshop.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Endpoints for dashboard analytics and metrics")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    @Operation(summary = "Get high-level dashboard metrics, analytics, charts, and recommendations")
    public ResponseEntity<DashboardSummary> getSummary() {
        return ResponseEntity.ok(dashboardService.getDashboardSummary());
    }
}
