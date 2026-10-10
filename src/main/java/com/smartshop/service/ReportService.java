package com.smartshop.service;

import com.smartshop.dto.ForecastResponse;
import com.smartshop.dto.ReportRow;
import com.smartshop.entity.Product;
import com.smartshop.entity.Sale;
import com.smartshop.repository.ProductRepository;
import com.smartshop.repository.SaleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final ForecastService forecastService;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter CSV_DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ReportService(SaleRepository saleRepository, ProductRepository productRepository, ForecastService forecastService) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.forecastService = forecastService;
    }

    public ReportRow.SalesSummaryReport getSalesSummary(LocalDate from, LocalDate to) {
        ZoneId shopZone = ZoneId.of("Asia/Kolkata");
        LocalDateTime start = (from != null) ? from.atStartOfDay() : LocalDate.now(shopZone).minusDays(30).atStartOfDay();
        LocalDateTime end = (to != null) ? to.plusDays(1).atStartOfDay() : LocalDate.now(shopZone).plusDays(1).atStartOfDay();

        List<Sale> sales = saleRepository.findSalesForExport(start, end);

        long totalTransactions = sales.size();
        long totalQuantitySold = sales.stream().mapToLong(Sale::getQuantitySold).sum();
        BigDecimal totalRevenue = sales.stream()
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Daily breakdowns
        Map<LocalDate, List<Sale>> byDay = sales.stream()
                .collect(Collectors.groupingBy(s -> s.getSaleDate().toLocalDate(), TreeMap::new, Collectors.toList()));

        List<ReportRow.DailySalesSummary> dailySales = new ArrayList<>();
        for (Map.Entry<LocalDate, List<Sale>> entry : byDay.entrySet()) {
            long dayTx = entry.getValue().size();
            long dayQty = entry.getValue().stream().mapToLong(Sale::getQuantitySold).sum();
            BigDecimal dayRev = entry.getValue().stream().map(Sale::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

            dailySales.add(ReportRow.DailySalesSummary.builder()
                    .date(entry.getKey().format(DATE_FMT))
                    .transactions(dayTx)
                    .quantitySold(dayQty)
                    .revenue(dayRev)
                    .build());
        }

        // Product-wise breakdowns
        Map<Product, List<Sale>> byProduct = sales.stream()
                .collect(Collectors.groupingBy(Sale::getProduct));

        List<ReportRow.ProductSalesSummary> productSales = new ArrayList<>();
        for (Map.Entry<Product, List<Sale>> entry : byProduct.entrySet()) {
            Product p = entry.getKey();
            long pQty = entry.getValue().stream().mapToLong(Sale::getQuantitySold).sum();
            BigDecimal pRev = entry.getValue().stream().map(Sale::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

            productSales.add(ReportRow.ProductSalesSummary.builder()
                    .productId(p.getId())
                    .productName(p.getName())
                    .category(p.getCategory())
                    .quantitySold(pQty)
                    .revenue(pRev)
                    .build());
        }
        productSales.sort(Comparator.comparing(ReportRow.ProductSalesSummary::getRevenue).reversed());

        return ReportRow.SalesSummaryReport.builder()
                .totalTransactions(totalTransactions)
                .totalQuantitySold(totalQuantitySold)
                .totalRevenue(totalRevenue)
                .dailySales(dailySales)
                .productSales(productSales)
                .build();
    }

    public List<ReportRow.InventoryReportItem> getInventoryReport() {
        List<Product> products = productRepository.findAll();
        List<ReportRow.InventoryReportItem> report = new ArrayList<>();

        for (Product p : products) {
            int stock = (p.getStockQuantity() != null) ? p.getStockQuantity() : 0;
            BigDecimal value = p.getPrice().multiply(BigDecimal.valueOf(stock));

            report.add(ReportRow.InventoryReportItem.builder()
                    .productId(p.getId())
                    .productName(p.getName())
                    .sku(p.getSku())
                    .category(p.getCategory())
                    .currentStock(stock)
                    .minStockLevel(p.getMinStockLevel())
                    .stockStatus(p.getStockStatus())
                    .unitPrice(p.getPrice())
                    .inventoryValue(value)
                    .build());
        }

        report.sort(Comparator.comparing(ReportRow.InventoryReportItem::getProductName));
        return report;
    }

    public List<ReportRow.ProductInsightItem> getProductInsights() {
        List<Product> products = productRepository.findAll();
        List<ForecastResponse> latestForecasts = forecastService.getLatestForecasts();
        Map<Long, ForecastResponse> forecastMap = latestForecasts.stream()
                .collect(Collectors.toMap(ForecastResponse::getProductId, f -> f, (a, b) -> a));

        List<ReportRow.ProductInsightItem> insights = new ArrayList<>();

        for (Product p : products) {
            List<Sale> sales = saleRepository.findByProductOrderBySaleDateAsc(p);
            long totalQty = sales.stream().mapToLong(Sale::getQuantitySold).sum();
            BigDecimal totalRev = sales.stream().map(Sale::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

            double avgDaily = 0.0;
            if (!sales.isEmpty()) {
                LocalDate firstDate = sales.get(0).getSaleDate().toLocalDate();
                long days = Math.max(1, java.time.temporal.ChronoUnit.DAYS.between(firstDate, LocalDate.now()) + 1);
                avgDaily = BigDecimal.valueOf((double) totalQty / days).setScale(2, RoundingMode.HALF_UP).doubleValue();
            }

            ForecastResponse fc = forecastMap.get(p.getId());
            Double predDemand = (fc != null) ? fc.getPredictedDemand() : 0.0;
            Integer restock = (fc != null) ? fc.getSuggestedRestock() : 0;
            String conf = (fc != null) ? fc.getConfidence() : "Low";
            Boolean overstock = (fc != null) ? fc.getOverstockRisk() : false;

            insights.add(ReportRow.ProductInsightItem.builder()
                    .productId(p.getId())
                    .productName(p.getName())
                    .sku(p.getSku())
                    .category(p.getCategory())
                    .totalQuantitySold(totalQty)
                    .totalRevenue(totalRev)
                    .avgDailyDemand(avgDaily)
                    .predictedDemand(predDemand)
                    .suggestedRestock(restock)
                    .confidence(conf)
                    .overstockRisk(overstock)
                    .build());
        }

        insights.sort(Comparator.comparing(ReportRow.ProductInsightItem::getTotalRevenue).reversed());
        return insights;
    }

    public byte[] exportCsv(String type, LocalDate from, LocalDate to) {
        StringBuilder csv = new StringBuilder();

        if ("sales".equalsIgnoreCase(type)) {
            csv.append("Product,Category,Quantity,Unit Price,Total Amount,Sale Date\n");
            ZoneId shopZone = ZoneId.of("Asia/Kolkata");
            LocalDateTime start = (from != null) ? from.atStartOfDay() : LocalDate.now(shopZone).minusDays(60).atStartOfDay();
            LocalDateTime end = (to != null) ? to.plusDays(1).atStartOfDay() : LocalDate.now(shopZone).plusDays(1).atStartOfDay();
            List<Sale> sales = saleRepository.findSalesForExport(start, end);

            for (Sale s : sales) {
                csv.append(escapeCsv(s.getProduct().getName())).append(",")
                   .append(escapeCsv(s.getProduct().getCategory())).append(",")
                   .append(s.getQuantitySold()).append(",")
                   .append(s.getUnitPrice()).append(",")
                   .append(s.getTotalAmount()).append(",")
                   .append(s.getSaleDate().format(CSV_DATETIME_FMT)).append("\n");
            }
        } else {
            csv.append("Product,SKU,Category,Current Stock,Minimum Stock,Price,Stock Status\n");
            List<Product> products = productRepository.findAll();

            for (Product p : products) {
                csv.append(escapeCsv(p.getName())).append(",")
                   .append(escapeCsv(p.getSku())).append(",")
                   .append(escapeCsv(p.getCategory())).append(",")
                   .append(p.getStockQuantity()).append(",")
                   .append(p.getMinStockLevel()).append(",")
                   .append(p.getPrice()).append(",")
                   .append(p.getStockStatus()).append("\n");
            }
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
