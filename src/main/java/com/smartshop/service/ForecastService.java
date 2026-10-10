package com.smartshop.service;

import com.smartshop.dto.ForecastResponse;
import com.smartshop.entity.DemandForecast;
import com.smartshop.entity.Product;
import com.smartshop.entity.Sale;
import com.smartshop.exception.ResourceNotFoundException;
import com.smartshop.repository.DemandForecastRepository;
import com.smartshop.repository.ProductRepository;
import com.smartshop.repository.SaleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ForecastService {

    private static final Logger log = LoggerFactory.getLogger(ForecastService.class);

    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;
    private final DemandForecastRepository demandForecastRepository;

    private static final int HISTORY_DAYS = 30;
    private static final int FORECAST_HORIZON_DAYS = 7;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ForecastService(ProductRepository productRepository, SaleRepository saleRepository, DemandForecastRepository demandForecastRepository) {
        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
        this.demandForecastRepository = demandForecastRepository;
    }

    /**
     * Generate demand forecast for a single product.
     */
    @Transactional
    public ForecastResponse generateForecastForProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        return calculateAndSaveForecast(product);
    }

    /**
     * Generate forecasts for all products.
     */
    @Transactional
    public List<ForecastResponse> generateForecastForAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ForecastResponse> responses = new ArrayList<>();

        for (Product product : products) {
            responses.add(calculateAndSaveForecast(product));
        }

        log.info("Completed demand forecast generation for {} products", responses.size());
        return responses;
    }

    /**
     * Nightly scheduled forecast job.
     */
    @Scheduled(cron = "${smartshop.forecast.cron:0 0 2 * * ?}")
    public void runScheduledForecasting() {
        log.info("Starting scheduled demand forecast generation...");
        try {
            generateForecastForAllProducts();
            log.info("Successfully finished scheduled demand forecast generation");
        } catch (Exception e) {
            log.error("Error during scheduled demand forecast generation", e);
        }
    }

    /**
     * Core forecasting calculation and persistence logic.
     */
    public ForecastResponse calculateAndSaveForecast(Product product) {
        List<Sale> allSales = saleRepository.findByProductOrderBySaleDateAsc(product);
        int totalSalesTransactions = allSales.size();

        // Step 1: Historical Data grouped by continuous calendar day (last 30 days)
        ZoneId shopZone = ZoneId.of("Asia/Kolkata");
        LocalDate today = LocalDate.now(shopZone);
        LocalDate startDate = today.minusDays(HISTORY_DAYS);

        Map<LocalDate, Double> dailyMap = new TreeMap<>();
        for (int i = 0; i <= HISTORY_DAYS; i++) {
            dailyMap.put(startDate.plusDays(i), 0.0);
        }

        for (Sale sale : allSales) {
            LocalDate sDate = sale.getSaleDate().toLocalDate();
            if (!sDate.isBefore(startDate) && !sDate.isAfter(today)) {
                dailyMap.put(sDate, dailyMap.getOrDefault(sDate, 0.0) + sale.getQuantitySold());
            }
        }

        List<Double> yValues = new ArrayList<>(dailyMap.values());
        int n = yValues.size(); // continuous historical points

        // Step 2: Linear Regression (least squares)
        double sumX = 0;
        double sumY = 0;
        double sumXY = 0;
        double sumX2 = 0;

        for (int i = 0; i < n; i++) {
            double x = i;
            double y = yValues.get(i);
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        double denominator = (n * sumX2 - sumX * sumX);
        double slope = 0.0;
        double intercept = 0.0;

        if (Math.abs(denominator) > 1e-9) {
            slope = (n * sumXY - sumX * sumY) / denominator;
            intercept = (sumY - slope * sumX) / n;
        } else {
            slope = 0.0;
            intercept = (n > 0) ? (sumY / n) : 0.0;
        }

        // Project next 7 days with regression
        double regressionProjection = 0.0;
        List<Double> regressionDailyValues = new ArrayList<>();
        for (int k = 0; k < FORECAST_HORIZON_DAYS; k++) {
            double dayIndex = n + k;
            double pred = slope * dayIndex + intercept;
            pred = Math.max(0.0, pred);
            regressionDailyValues.add(pred);
            regressionProjection += pred;
        }
        regressionProjection = Math.max(0.0, regressionProjection);

        // Step 3: Moving Average (7-day moving average)
        double last7DaysSum = 0.0;
        int maWindow = Math.min(7, n);
        for (int i = n - maWindow; i < n; i++) {
            if (i >= 0) {
                last7DaysSum += yValues.get(i);
            }
        }
        double movingAverageDaily = (maWindow > 0) ? (last7DaysSum / maWindow) : 0.0;
        double movingAverageProjection = movingAverageDaily * FORECAST_HORIZON_DAYS;
        movingAverageProjection = Math.max(0.0, movingAverageProjection);

        // Step 4: Blend
        // Predicted 7-Day Demand = 0.6 * Regression Projection + 0.4 * Moving Average Projection
        double blendedDemand = 0.6 * regressionProjection + 0.4 * movingAverageProjection;
        blendedDemand = Math.max(0.0, blendedDemand);

        // Round to 2 decimal places
        BigDecimal bd = BigDecimal.valueOf(blendedDemand).setScale(2, RoundingMode.HALF_UP);
        double predictedDemand = bd.doubleValue();

        // Step 5: Restock Recommendation
        // suggestedRestock = max(0, ceil(predictedDemand * 1.1 - currentStock))
        int currentStock = (product.getStockQuantity() != null) ? product.getStockQuantity() : 0;
        int suggestedRestock = (int) Math.max(0, Math.ceil(predictedDemand * 1.1 - currentStock));

        // Step 6: Confidence
        // Fewer than 7 sales records -> Low, 7 to 20 -> Medium, More than 20 -> High
        String confidence;
        if (totalSalesTransactions < 7) {
            confidence = "Low";
        } else if (totalSalesTransactions <= 20) {
            confidence = "Medium";
        } else {
            confidence = "High";
        }

        // Step 7: Overstock Risk
        // currentStock > 3 * predictedDemand
        boolean overstockRisk = (currentStock > (3 * predictedDemand)) && (currentStock > 0);

        // Step 8: Save or update forecast in demand_forecasts table (avoid duplicate rows for same day)
        LocalDateTime startOfToday = today.atStartOfDay();
        LocalDateTime endOfToday = today.plusDays(1).atStartOfDay();
        Optional<DemandForecast> existingToday = demandForecastRepository.findFirstByProductIdAndForecastDateBetween(product.getId(), startOfToday, endOfToday);

        DemandForecast demandForecast;
        if (existingToday.isPresent()) {
            demandForecast = existingToday.get();
            demandForecast.setPredictedDemand(predictedDemand);
            demandForecast.setSuggestedRestock(suggestedRestock);
            demandForecast.setConfidence(confidence);
            demandForecast.setForecastDate(LocalDateTime.now());
        } else {
            demandForecast = DemandForecast.builder()
                    .product(product)
                    .predictedDemand(predictedDemand)
                    .suggestedRestock(suggestedRestock)
                    .confidence(confidence)
                    .forecastDate(LocalDateTime.now())
                    .build();
        }
        demandForecast = demandForecastRepository.save(demandForecast);

        // Prepare chart historical points
        List<ForecastResponse.DailySalesPoint> historicalPoints = new ArrayList<>();
        for (Map.Entry<LocalDate, Double> entry : dailyMap.entrySet()) {
            historicalPoints.add(ForecastResponse.DailySalesPoint.builder()
                    .date(entry.getKey().format(DATE_FMT))
                    .quantity(entry.getValue())
                    .build());
        }

        // Prepare chart forecast points for next 7 days
        List<ForecastResponse.DailyForecastPoint> forecastPoints = new ArrayList<>();
        for (int d = 1; d <= FORECAST_HORIZON_DAYS; d++) {
            LocalDate fDate = today.plusDays(d);
            double regVal = (d - 1 < regressionDailyValues.size()) ? regressionDailyValues.get(d - 1) : movingAverageDaily;
            double dayPred = Math.max(0.0, 0.6 * regVal + 0.4 * movingAverageDaily);
            forecastPoints.add(ForecastResponse.DailyForecastPoint.builder()
                    .date(fDate.format(DATE_FMT))
                    .predictedQuantity(BigDecimal.valueOf(dayPred).setScale(2, RoundingMode.HALF_UP).doubleValue())
                    .build());
        }

        return ForecastResponse.builder()
                .productId(product.getId())
                .productName(product.getName())
                .sku(product.getSku())
                .category(product.getCategory())
                .currentStock(currentStock)
                .minStockLevel(product.getMinStockLevel())
                .predictedDemand(predictedDemand)
                .suggestedRestock(suggestedRestock)
                .confidence(confidence)
                .overstockRisk(overstockRisk)
                .regressionProjection(BigDecimal.valueOf(regressionProjection).setScale(2, RoundingMode.HALF_UP).doubleValue())
                .movingAverageProjection(BigDecimal.valueOf(movingAverageProjection).setScale(2, RoundingMode.HALF_UP).doubleValue())
                .historicalSalesCount(totalSalesTransactions)
                .forecastDate(demandForecast.getForecastDate())
                .historicalPoints(historicalPoints)
                .forecastPoints(forecastPoints)
                .build();
    }

    /**
     * Retrieve the latest forecasts for all products.
     */
    public List<ForecastResponse> getLatestForecasts() {
        List<Product> products = productRepository.findAll();
        List<ForecastResponse> results = new ArrayList<>();

        for (Product product : products) {
            Optional<DemandForecast> latest = demandForecastRepository.findTopByProductOrderByForecastDateDesc(product);
            if (latest.isPresent()) {
                DemandForecast df = latest.get();
                int currentStock = (product.getStockQuantity() != null) ? product.getStockQuantity() : 0;
                boolean overstockRisk = (currentStock > (3 * df.getPredictedDemand())) && (currentStock > 0);

                results.add(ForecastResponse.builder()
                        .productId(product.getId())
                        .productName(product.getName())
                        .sku(product.getSku())
                        .category(product.getCategory())
                        .currentStock(currentStock)
                        .minStockLevel(product.getMinStockLevel())
                        .predictedDemand(df.getPredictedDemand())
                        .suggestedRestock(df.getSuggestedRestock())
                        .confidence(df.getConfidence())
                        .overstockRisk(overstockRisk)
                        .forecastDate(df.getForecastDate())
                        .build());
            } else {
                results.add(calculateAndSaveForecast(product));
            }
        }

        return results;
    }

    public ForecastResponse getForecastForProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        return calculateAndSaveForecast(product);
    }
}
