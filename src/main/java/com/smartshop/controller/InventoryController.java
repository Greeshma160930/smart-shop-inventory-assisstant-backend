package com.smartshop.controller;

import com.smartshop.dto.AdjustmentRequest;
import com.smartshop.dto.RestockRequest;
import com.smartshop.entity.InventoryLog;
import com.smartshop.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@Tag(name = "Inventory", description = "Endpoints for stock restocking, count adjustments, and inventory change logs")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/restock")
    @Operation(summary = "Restock product inventory and record inventory log")
    public ResponseEntity<InventoryLog> restockProduct(@Valid @RequestBody RestockRequest request) {
        return ResponseEntity.ok(inventoryService.restockProduct(request));
    }

    @PostMapping("/adjust")
    @Operation(summary = "Adjust physical product stock count and record inventory log")
    public ResponseEntity<InventoryLog> adjustProduct(@Valid @RequestBody AdjustmentRequest request) {
        return ResponseEntity.ok(inventoryService.adjustProduct(request));
    }

    @GetMapping("/logs")
    @Operation(summary = "Get inventory history timeline and audit logs")
    public ResponseEntity<Page<InventoryLog>> getInventoryLogs(
            @RequestParam(required = false) Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "logDate"));
        return ResponseEntity.ok(inventoryService.getInventoryLogs(productId, pageable));
    }
}
