package com.smartshop.controller;

import com.smartshop.dto.SaleRequest;
import com.smartshop.entity.Sale;
import com.smartshop.service.SalesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
@Tag(name = "Sales", description = "Endpoints for recording sales and viewing sales history")
public class SalesController {

    private final SalesService salesService;

    public SalesController(SalesService salesService) {
        this.salesService = salesService;
    }

    @PostMapping
    @Operation(summary = "Record a new sale transaction")
    public ResponseEntity<Sale> recordSale(@Valid @RequestBody SaleRequest request) {
        Sale sale = salesService.recordSale(request);
        return new ResponseEntity<>(sale, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get sales history with optional date and product filters")
    public ResponseEntity<Page<Sale>> getSales(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "saleDate"));
        return ResponseEntity.ok(salesService.getSales(productId, from, to, pageable));
    }

    @GetMapping("/recent")
    @Operation(summary = "Get most recent 10 sales")
    public ResponseEntity<List<Sale>> getRecentSales() {
        return ResponseEntity.ok(salesService.getRecentSales());
    }
}
