package com.smartshop.service;

import com.smartshop.dto.ForecastResponse;
import com.smartshop.entity.DemandForecast;
import com.smartshop.entity.Product;
import com.smartshop.entity.Sale;
import com.smartshop.repository.DemandForecastRepository;
import com.smartshop.repository.ProductRepository;
import com.smartshop.repository.SaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ForecastServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private DemandForecastRepository demandForecastRepository;

    @InjectMocks
    private ForecastService forecastService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .name("Basmati Rice 5kg")
                .sku("SKU-RICE-001")
                .category("Food Grains")
                .price(BigDecimal.valueOf(550.00))
                .stockQuantity(15)
                .minStockLevel(20)
                .supplier("Agro Farms India")
                .build();

        lenient().when(demandForecastRepository.save(any(DemandForecast.class)))
                .thenAnswer(invocation -> {
                    DemandForecast df = invocation.getArgument(0);
                    df.setId(101L);
                    if (df.getForecastDate() == null) {
                        df.setForecastDate(LocalDateTime.now());
                    }
                    return df;
                });
    }

    @Test
    @DisplayName("Test forecast calculation with steady upward sales trend")
    void testForecastCalculationWithSteadySales() {
        List<Sale> sales = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        // Generate 25 daily sales records with quantities 5, 6, 7, ...
        for (int i = 24; i >= 0; i--) {
            sales.add(Sale.builder()
                    .id((long) (i + 1))
                    .product(testProduct)
                    .quantitySold(5 + (24 - i) / 5)
                    .unitPrice(BigDecimal.valueOf(550.00))
                    .totalAmount(BigDecimal.valueOf(550.00 * (5 + (24 - i) / 5)))
                    .saleDate(now.minusDays(i))
                    .build());
        }

        when(saleRepository.findByProductOrderBySaleDateAsc(testProduct)).thenReturn(sales);

        ForecastResponse response = forecastService.calculateAndSaveForecast(testProduct);

        assertNotNull(response);
        assertEquals(testProduct.getId(), response.getProductId());
        assertTrue(response.getPredictedDemand() > 0, "Predicted demand should be positive");
        assertEquals("High", response.getConfidence(), "More than 20 sales should give High confidence");
        assertNotNull(response.getSuggestedRestock());
        assertFalse(response.getForecastPoints().isEmpty());
        assertEquals(7, response.getForecastPoints().size());
        verify(demandForecastRepository, times(1)).save(any(DemandForecast.class));
    }

    @Test
    @DisplayName("Test confidence levels based on number of sales records")
    void testConfidenceLevels() {
        LocalDateTime now = LocalDateTime.now();

        // 1. Low confidence (< 7 records)
        List<Sale> fewSales = new ArrayList<>();
        for (int i = 3; i >= 0; i--) {
            fewSales.add(Sale.builder()
                    .product(testProduct)
                    .quantitySold(2)
                    .saleDate(now.minusDays(i))
                    .build());
        }
        when(saleRepository.findByProductOrderBySaleDateAsc(testProduct)).thenReturn(fewSales);
        ForecastResponse lowConfResponse = forecastService.calculateAndSaveForecast(testProduct);
        assertEquals("Low", lowConfResponse.getConfidence());

        // 2. Medium confidence (7 to 20 records)
        List<Sale> medSales = new ArrayList<>();
        for (int i = 12; i >= 0; i--) {
            medSales.add(Sale.builder()
                    .product(testProduct)
                    .quantitySold(3)
                    .saleDate(now.minusDays(i))
                    .build());
        }
        when(saleRepository.findByProductOrderBySaleDateAsc(testProduct)).thenReturn(medSales);
        ForecastResponse medConfResponse = forecastService.calculateAndSaveForecast(testProduct);
        assertEquals("Medium", medConfResponse.getConfidence());

        // 3. High confidence (> 20 records)
        List<Sale> highSales = new ArrayList<>();
        for (int i = 25; i >= 0; i--) {
            highSales.add(Sale.builder()
                    .product(testProduct)
                    .quantitySold(4)
                    .saleDate(now.minusDays(i))
                    .build());
        }
        when(saleRepository.findByProductOrderBySaleDateAsc(testProduct)).thenReturn(highSales);
        ForecastResponse highConfResponse = forecastService.calculateAndSaveForecast(testProduct);
        assertEquals("High", highConfResponse.getConfidence());
    }

    @Test
    @DisplayName("Test non-negative prediction when sales are zero")
    void testNonNegativePredictionWhenNoSales() {
        when(saleRepository.findByProductOrderBySaleDateAsc(testProduct)).thenReturn(new ArrayList<>());

        ForecastResponse response = forecastService.calculateAndSaveForecast(testProduct);

        assertNotNull(response);
        assertEquals(0.0, response.getPredictedDemand());
        assertEquals(0, response.getSuggestedRestock());
        assertEquals("Low", response.getConfidence());
    }

    @Test
    @DisplayName("Test restock recommendation calculation formula")
    void testRestockRecommendationFormula() {
        // suggestedRestock = max(0, ceil(predictedDemand * 1.1 - currentStock))
        testProduct.setStockQuantity(10);

        List<Sale> sales = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        // Constant 5 sales per day for 10 days
        for (int i = 9; i >= 0; i--) {
            sales.add(Sale.builder()
                    .product(testProduct)
                    .quantitySold(5)
                    .saleDate(now.minusDays(i))
                    .build());
        }
        when(saleRepository.findByProductOrderBySaleDateAsc(testProduct)).thenReturn(sales);

        ForecastResponse response = forecastService.calculateAndSaveForecast(testProduct);

        // Expected restock = max(0, ceil(predictedDemand * 1.1 - currentStock))
        int expectedRestock = (int) Math.max(0, Math.ceil(response.getPredictedDemand() * 1.1 - testProduct.getStockQuantity()));
        assertEquals(expectedRestock, response.getSuggestedRestock());
    }

    @Test
    @DisplayName("Test overstock risk detection when stock exceeds 3x predicted demand")
    void testOverstockRiskDetection() {
        testProduct.setStockQuantity(200); // Very high stock

        List<Sale> sales = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        // Small sales: 1 per day for 8 days (predicted demand will be ~7)
        for (int i = 7; i >= 0; i--) {
            sales.add(Sale.builder()
                    .product(testProduct)
                    .quantitySold(1)
                    .saleDate(now.minusDays(i))
                    .build());
        }
        when(saleRepository.findByProductOrderBySaleDateAsc(testProduct)).thenReturn(sales);

        ForecastResponse response = forecastService.calculateAndSaveForecast(testProduct);

        assertTrue(response.getOverstockRisk(), "Should detect overstock risk when stock is 200 and demand is ~7");
        assertEquals(0, response.getSuggestedRestock(), "Suggested restock should be 0 when overstocked");
    }
}
