package com.smartshop.controller;

import com.smartshop.dto.ReportRow;
import com.smartshop.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reports", description = "Endpoints for sales, inventory, and insights reports with CSV export")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/sales-summary")
    @Operation(summary = "Get sales summary report for date range")
    public ResponseEntity<ReportRow.SalesSummaryReport> getSalesSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(reportService.getSalesSummary(from, to));
    }

    @GetMapping("/inventory")
    @Operation(summary = "Get inventory status and valuation report")
    public ResponseEntity<List<ReportRow.InventoryReportItem>> getInventoryReport() {
        return ResponseEntity.ok(reportService.getInventoryReport());
    }

    @GetMapping("/product-insights")
    @Operation(summary = "Get deep product insights and AI demand analysis")
    public ResponseEntity<List<ReportRow.ProductInsightItem>> getProductInsights() {
        return ResponseEntity.ok(reportService.getProductInsights());
    }

    @GetMapping("/export")
    @Operation(summary = "Export report data as CSV file (type=sales or type=inventory)")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(defaultValue = "sales") String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        byte[] csvBytes = reportService.exportCsv(type, from, to);
        String filename = type.equalsIgnoreCase("inventory") ? "smart_shop_inventory.csv" : "smart_shop_sales.csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvBytes);
    }
}
