package com.smartshop.controller;

import com.smartshop.entity.Alert;
import com.smartshop.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
@Tag(name = "Alerts", description = "Endpoints for managing inventory stock alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    @Operation(summary = "Get list of alerts with optional unread filter and pagination")
    public ResponseEntity<Page<Alert>> getAlerts(
            @RequestParam(required = false, defaultValue = "false") Boolean unreadOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(alertService.getAlerts(unreadOnly, pageable));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active unread alerts list")
    public ResponseEntity<java.util.List<Alert>> getActiveAlerts() {
        return ResponseEntity.ok(alertService.getUnreadAlerts());
    }

    @GetMapping("/count")
    @Operation(summary = "Get total unread alerts count for navbar bell")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        return ResponseEntity.ok(Map.of("unreadCount", alertService.getUnreadCount()));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark a specific alert as read")
    public ResponseEntity<Alert> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.markAsRead(id));
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark all unread alerts as read")
    public ResponseEntity<Map<String, String>> markAllAsRead() {
        alertService.markAllAsRead();
        return ResponseEntity.ok(Map.of("message", "All alerts marked as read"));
    }
}
