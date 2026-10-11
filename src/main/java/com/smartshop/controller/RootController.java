package com.smartshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@Tag(name = "Root", description = "Root health check and status endpoint")
public class RootController {

    @GetMapping("/")
    @Operation(summary = "Root health check endpoint")
    public ResponseEntity<Map<String, Object>> root() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "UP");
        response.put("message", "Smart Shop Inventory Assistant backend is running");
        return ResponseEntity.ok(response);
    }
}
