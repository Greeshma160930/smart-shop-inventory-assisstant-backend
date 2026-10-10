package com.smartshop.controller;

import com.smartshop.dto.ForecastResponse;
import com.smartshop.service.ForecastService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forecast")
@Tag(name = "AI Forecast", description = "Endpoints for demand forecasting and restock recommendations")
public class ForecastController {

    private final ForecastService forecastService;

    public ForecastController(ForecastService forecastService) {
        this.forecastService = forecastService;
    }

    @PostMapping("/{productId}")
    @Operation(summary = "Generate and update demand forecast for a single product")
    public ResponseEntity<ForecastResponse> generateForecast(@PathVariable Long productId) {
        return ResponseEntity.ok(forecastService.generateForecastForProduct(productId));
    }

    @PostMapping("/all")
    @Operation(summary = "Generate and update demand forecast for all products")
    public ResponseEntity<List<ForecastResponse>> generateAllForecasts() {
        return ResponseEntity.ok(forecastService.generateForecastForAllProducts());
    }

    @GetMapping("/latest")
    @Operation(summary = "Get the latest demand forecasts for all products")
    public ResponseEntity<List<ForecastResponse>> getLatestForecasts() {
        return ResponseEntity.ok(forecastService.getLatestForecasts());
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get the latest forecast for a specific product")
    public ResponseEntity<ForecastResponse> getProductForecast(@PathVariable Long productId) {
        return ResponseEntity.ok(forecastService.getForecastForProduct(productId));
    }
}
