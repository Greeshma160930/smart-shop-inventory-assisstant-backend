package com.smartshop.service;

import com.smartshop.dto.DashboardSummary;
import com.smartshop.dto.ForecastResponse;
import com.smartshop.entity.Alert;
import com.smartshop.entity.Product;
import com.smartshop.entity.Sale;
import com.smartshop.repository.AlertRepository;
import com.smartshop.repository.DemandForecastRepository;
import com.smartshop.repository.ProductRepository;
import com.smartshop.repository.SaleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);

    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;
    private final AlertRepository alertRepository;
    private final DemandForecastRepository demandForecastRepository;
    private final ForecastService forecastService;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public DashboardService(ProductRepository productRepository, SaleRepository saleRepository, AlertRepository alertRepository, DemandForecastRepository demandForecastRepository, ForecastService forecastService) {
        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
        this.alertRepository = alertRepository;
        this.demandForecastRepository = demandForecastRepository;
        this.forecastService = forecastService;
    }

    public DashboardSummary getDashboardSummary() {
        long totalProducts = productRepository.count();

        ZoneId shopZone = ZoneId.of("Asia/Kolkata");
        LocalDate today = LocalDate.now(shopZone);
        LocalDateTime startOfToday = today.atStartOfDay();
        LocalDateTime startOfTomorrow = today.plusDays(1).atStartOfDay();

        BigDecimal salesToday = saleRepository.sumTotalSalesBetween(startOfToday, startOfTomorrow);
        if (salesToday == null) salesToday = BigDecimal.ZERO;

        LocalDate yesterday = today.minusDays(1);
        LocalDateTime startOfYesterday = yesterday.atStartOfDay();
        BigDecimal salesYesterday = saleRepository.sumTotalSalesBetween(startOfYesterday, startOfToday);
        if (salesYesterday == null) salesYesterday = BigDecimal.ZERO;

        double salesGrowth = 0.0;
        if (salesYesterday.compareTo(BigDecimal.ZERO) > 0) {
            salesGrowth = salesToday.subtract(salesYesterday)
                    .divide(salesYesterday, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        long lowStockCount = productRepository.countLowStockProducts();
        long outOfStockCount = productRepository.countOutOfStockProducts();

        Double predictedDemand = demandForecastRepository.sumTotalPredictedDemandNext7Days();
        if (predictedDemand == null || predictedDemand <= 0) {
            List<ForecastResponse> latest = forecastService.getLatestForecasts();
            predictedDemand = latest.stream().mapToDouble(ForecastResponse::getPredictedDemand).sum();
        }

        // 1. Sales Trend: last 14 days
        List<DashboardSummary.SalesTrendPoint> salesTrend = new ArrayList<>();
        for (int i = 13; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            LocalDateTime sStart = d.atStartOfDay();
            LocalDateTime sEnd = d.plusDays(1).atStartOfDay();

            BigDecimal dayRev = saleRepository.sumTotalSalesBetween(sStart, sEnd);
            long dayQty = saleRepository.sumQuantitySoldBetween(sStart, sEnd);

            salesTrend.add(DashboardSummary.SalesTrendPoint.builder()
                    .date(d.format(DATE_FMT))
                    .revenue(dayRev != null ? dayRev : BigDecimal.ZERO)
                    .quantitySold(dayQty)
                    .build());
        }

        // 2. Category-wise Stock
        List<Product> allProducts = productRepository.findAll();
        Map<String, List<Product>> byCategory = allProducts.stream()
                .collect(Collectors.groupingBy(Product::getCategory));

        List<DashboardSummary.CategoryStockPoint> categoryStock = new ArrayList<>();
        for (Map.Entry<String, List<Product>> entry : byCategory.entrySet()) {
            long pCount = entry.getValue().size();
            long tStock = entry.getValue().stream().mapToLong(p -> p.getStockQuantity() != null ? p.getStockQuantity() : 0).sum();
            BigDecimal tVal = entry.getValue().stream()
                    .map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getStockQuantity() != null ? p.getStockQuantity() : 0)))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            categoryStock.add(DashboardSummary.CategoryStockPoint.builder()
                    .category(entry.getKey())
                    .productCount(pCount)
                    .totalStock(tStock)
                    .totalValue(tVal)
                    .build());
        }

        // 3. Stock Level Distribution
        long inStockCount = allProducts.stream()
                .filter(p -> p.getStockQuantity() != null && p.getStockQuantity() > p.getMinStockLevel())
                .count();
        DashboardSummary.StockLevelDistribution stockDistribution = DashboardSummary.StockLevelDistribution.builder()
                .inStock(inStockCount)
                .lowStock(lowStockCount - outOfStockCount)
                .outOfStock(outOfStockCount)
                .build();

        // 4. AI Restocking Suggestions
        List<ForecastResponse> latestForecasts = forecastService.getLatestForecasts();
        List<ForecastResponse> restockingSuggestions = latestForecasts.stream()
                .filter(f -> f.getSuggestedRestock() != null && f.getSuggestedRestock() > 0)
                .sorted(Comparator.comparing(ForecastResponse::getSuggestedRestock).reversed())
                .limit(6)
                .collect(Collectors.toList());

        // 5. Recent Sales
        List<Sale> recentSalesList = saleRepository.findTop10ByOrderBySaleDateDesc();
        List<DashboardSummary.RecentSaleDto> recentSales = recentSalesList.stream()
                .map(s -> DashboardSummary.RecentSaleDto.builder()
                        .id(s.getId())
                        .productName(s.getProduct().getName())
                        .sku(s.getProduct().getSku())
                        .quantitySold(s.getQuantitySold())
                        .unitPrice(s.getUnitPrice())
                        .totalAmount(s.getTotalAmount())
                        .saleDate(s.getSaleDate().format(TIME_FMT))
                        .userName(s.getUser() != null ? s.getUser().getName() : "Store Staff")
                        .build())
                .collect(Collectors.toList());

        // 6. Active Alerts
        List<Alert> unreadAlerts = alertRepository.findByIsReadFalseOrderByCreatedAtDesc();
        List<DashboardSummary.AlertDto> activeAlerts = unreadAlerts.stream()
                .limit(6)
                .map(a -> DashboardSummary.AlertDto.builder()
                        .id(a.getId())
                        .productId(a.getProduct().getId())
                        .productName(a.getProduct().getName())
                        .sku(a.getProduct().getSku())
                        .type(a.getType())
                        .message(a.getMessage())
                        .isRead(a.getIsRead())
                        .createdAt(a.getCreatedAt().format(TIME_FMT))
                        .build())
                .collect(Collectors.toList());

        return DashboardSummary.builder()
                .totalProducts(totalProducts)
                .totalSalesToday(salesToday)
                .lowStockCount(lowStockCount)
                .outOfStockCount(outOfStockCount)
                .predictedDemandNext7Days(BigDecimal.valueOf(predictedDemand).setScale(1, RoundingMode.HALF_UP).doubleValue())
                .salesTodayGrowth(salesGrowth)
                .salesTrend(salesTrend)
                .categoryStock(categoryStock)
                .stockLevels(stockDistribution)
                .restockingSuggestions(restockingSuggestions)
                .recentSales(recentSales)
                .activeAlerts(activeAlerts)
                .build();
    }
}
