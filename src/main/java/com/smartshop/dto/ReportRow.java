package com.smartshop.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ReportRow {

    public static class SalesSummaryReport {
        private Long totalTransactions;
        private Long totalQuantitySold;
        private BigDecimal totalRevenue;
        private List<DailySalesSummary> dailySales = new ArrayList<>();
        private List<ProductSalesSummary> productSales = new ArrayList<>();

        public SalesSummaryReport() {}
        public SalesSummaryReport(Long totalTransactions, Long totalQuantitySold, BigDecimal totalRevenue, List<DailySalesSummary> dailySales, List<ProductSalesSummary> productSales) {
            this.totalTransactions = totalTransactions;
            this.totalQuantitySold = totalQuantitySold;
            this.totalRevenue = totalRevenue;
            this.dailySales = (dailySales != null) ? dailySales : new ArrayList<>();
            this.productSales = (productSales != null) ? productSales : new ArrayList<>();
        }

        public static SalesSummaryReportBuilder builder() { return new SalesSummaryReportBuilder(); }
        public static class SalesSummaryReportBuilder {
            private Long totalTransactions;
            private Long totalQuantitySold;
            private BigDecimal totalRevenue;
            private List<DailySalesSummary> dailySales = new ArrayList<>();
            private List<ProductSalesSummary> productSales = new ArrayList<>();
            public SalesSummaryReportBuilder totalTransactions(Long totalTransactions) { this.totalTransactions = totalTransactions; return this; }
            public SalesSummaryReportBuilder totalQuantitySold(Long totalQuantitySold) { this.totalQuantitySold = totalQuantitySold; return this; }
            public SalesSummaryReportBuilder totalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; return this; }
            public SalesSummaryReportBuilder dailySales(List<DailySalesSummary> dailySales) { this.dailySales = dailySales; return this; }
            public SalesSummaryReportBuilder productSales(List<ProductSalesSummary> productSales) { this.productSales = productSales; return this; }
            public SalesSummaryReport build() { return new SalesSummaryReport(totalTransactions, totalQuantitySold, totalRevenue, dailySales, productSales); }
        }

        public Long getTotalTransactions() { return totalTransactions; }
        public void setTotalTransactions(Long totalTransactions) { this.totalTransactions = totalTransactions; }
        public Long getTotalQuantitySold() { return totalQuantitySold; }
        public void setTotalQuantitySold(Long totalQuantitySold) { this.totalQuantitySold = totalQuantitySold; }
        public BigDecimal getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
        public List<DailySalesSummary> getDailySales() { return dailySales; }
        public void setDailySales(List<DailySalesSummary> dailySales) { this.dailySales = dailySales; }
        public List<ProductSalesSummary> getProductSales() { return productSales; }
        public void setProductSales(List<ProductSalesSummary> productSales) { this.productSales = productSales; }
    }

    public static class DailySalesSummary {
        private String date;
        private Long transactions;
        private Long quantitySold;
        private BigDecimal revenue;

        public DailySalesSummary() {}
        public DailySalesSummary(String date, Long transactions, Long quantitySold, BigDecimal revenue) {
            this.date = date;
            this.transactions = transactions;
            this.quantitySold = quantitySold;
            this.revenue = revenue;
        }

        public static DailySalesSummaryBuilder builder() { return new DailySalesSummaryBuilder(); }
        public static class DailySalesSummaryBuilder {
            private String date;
            private Long transactions;
            private Long quantitySold;
            private BigDecimal revenue;
            public DailySalesSummaryBuilder date(String date) { this.date = date; return this; }
            public DailySalesSummaryBuilder transactions(Long transactions) { this.transactions = transactions; return this; }
            public DailySalesSummaryBuilder quantitySold(Long quantitySold) { this.quantitySold = quantitySold; return this; }
            public DailySalesSummaryBuilder revenue(BigDecimal revenue) { this.revenue = revenue; return this; }
            public DailySalesSummary build() { return new DailySalesSummary(date, transactions, quantitySold, revenue); }
        }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public Long getTransactions() { return transactions; }
        public void setTransactions(Long transactions) { this.transactions = transactions; }
        public Long getQuantitySold() { return quantitySold; }
        public void setQuantitySold(Long quantitySold) { this.quantitySold = quantitySold; }
        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
    }

    public static class ProductSalesSummary {
        private Long productId;
        private String productName;
        private String category;
        private Long quantitySold;
        private BigDecimal revenue;

        public ProductSalesSummary() {}
        public ProductSalesSummary(Long productId, String productName, String category, Long quantitySold, BigDecimal revenue) {
            this.productId = productId;
            this.productName = productName;
            this.category = category;
            this.quantitySold = quantitySold;
            this.revenue = revenue;
        }

        public static ProductSalesSummaryBuilder builder() { return new ProductSalesSummaryBuilder(); }
        public static class ProductSalesSummaryBuilder {
            private Long productId;
            private String productName;
            private String category;
            private Long quantitySold;
            private BigDecimal revenue;
            public ProductSalesSummaryBuilder productId(Long productId) { this.productId = productId; return this; }
            public ProductSalesSummaryBuilder productName(String productName) { this.productName = productName; return this; }
            public ProductSalesSummaryBuilder category(String category) { this.category = category; return this; }
            public ProductSalesSummaryBuilder quantitySold(Long quantitySold) { this.quantitySold = quantitySold; return this; }
            public ProductSalesSummaryBuilder revenue(BigDecimal revenue) { this.revenue = revenue; return this; }
            public ProductSalesSummary build() { return new ProductSalesSummary(productId, productName, category, quantitySold, revenue); }
        }

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public Long getQuantitySold() { return quantitySold; }
        public void setQuantitySold(Long quantitySold) { this.quantitySold = quantitySold; }
        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
    }

    public static class InventoryReportItem {
        private Long productId;
        private String productName;
        private String sku;
        private String category;
        private Integer currentStock;
        private Integer minStockLevel;
        private String stockStatus;
        private BigDecimal unitPrice;
        private BigDecimal inventoryValue;

        public InventoryReportItem() {}
        public InventoryReportItem(Long productId, String productName, String sku, String category, Integer currentStock, Integer minStockLevel, String stockStatus, BigDecimal unitPrice, BigDecimal inventoryValue) {
            this.productId = productId;
            this.productName = productName;
            this.sku = sku;
            this.category = category;
            this.currentStock = currentStock;
            this.minStockLevel = minStockLevel;
            this.stockStatus = stockStatus;
            this.unitPrice = unitPrice;
            this.inventoryValue = inventoryValue;
        }

        public static InventoryReportItemBuilder builder() { return new InventoryReportItemBuilder(); }
        public static class InventoryReportItemBuilder {
            private Long productId;
            private String productName;
            private String sku;
            private String category;
            private Integer currentStock;
            private Integer minStockLevel;
            private String stockStatus;
            private BigDecimal unitPrice;
            private BigDecimal inventoryValue;
            public InventoryReportItemBuilder productId(Long productId) { this.productId = productId; return this; }
            public InventoryReportItemBuilder productName(String productName) { this.productName = productName; return this; }
            public InventoryReportItemBuilder sku(String sku) { this.sku = sku; return this; }
            public InventoryReportItemBuilder category(String category) { this.category = category; return this; }
            public InventoryReportItemBuilder currentStock(Integer currentStock) { this.currentStock = currentStock; return this; }
            public InventoryReportItemBuilder minStockLevel(Integer minStockLevel) { this.minStockLevel = minStockLevel; return this; }
            public InventoryReportItemBuilder stockStatus(String stockStatus) { this.stockStatus = stockStatus; return this; }
            public InventoryReportItemBuilder unitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; return this; }
            public InventoryReportItemBuilder inventoryValue(BigDecimal inventoryValue) { this.inventoryValue = inventoryValue; return this; }
            public InventoryReportItem build() { return new InventoryReportItem(productId, productName, sku, category, currentStock, minStockLevel, stockStatus, unitPrice, inventoryValue); }
        }

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public String getSku() { return sku; }
        public void setSku(String sku) { this.sku = sku; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public Integer getCurrentStock() { return currentStock; }
        public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }
        public Integer getMinStockLevel() { return minStockLevel; }
        public void setMinStockLevel(Integer minStockLevel) { this.minStockLevel = minStockLevel; }
        public String getStockStatus() { return stockStatus; }
        public void setStockStatus(String stockStatus) { this.stockStatus = stockStatus; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
        public BigDecimal getInventoryValue() { return inventoryValue; }
        public void setInventoryValue(BigDecimal inventoryValue) { this.inventoryValue = inventoryValue; }
    }

    public static class ProductInsightItem {
        private Long productId;
        private String productName;
        private String sku;
        private String category;
        private Long totalQuantitySold;
        private BigDecimal totalRevenue;
        private Double avgDailyDemand;
        private Double predictedDemand;
        private Integer suggestedRestock;
        private String confidence;
        private Boolean overstockRisk;

        public ProductInsightItem() {}
        public ProductInsightItem(Long productId, String productName, String sku, String category, Long totalQuantitySold, BigDecimal totalRevenue, Double avgDailyDemand, Double predictedDemand, Integer suggestedRestock, String confidence, Boolean overstockRisk) {
            this.productId = productId;
            this.productName = productName;
            this.sku = sku;
            this.category = category;
            this.totalQuantitySold = totalQuantitySold;
            this.totalRevenue = totalRevenue;
            this.avgDailyDemand = avgDailyDemand;
            this.predictedDemand = predictedDemand;
            this.suggestedRestock = suggestedRestock;
            this.confidence = confidence;
            this.overstockRisk = overstockRisk;
        }

        public static ProductInsightItemBuilder builder() { return new ProductInsightItemBuilder(); }
        public static class ProductInsightItemBuilder {
            private Long productId;
            private String productName;
            private String sku;
            private String category;
            private Long totalQuantitySold;
            private BigDecimal totalRevenue;
            private Double avgDailyDemand;
            private Double predictedDemand;
            private Integer suggestedRestock;
            private String confidence;
            private Boolean overstockRisk;
            public ProductInsightItemBuilder productId(Long productId) { this.productId = productId; return this; }
            public ProductInsightItemBuilder productName(String productName) { this.productName = productName; return this; }
            public ProductInsightItemBuilder sku(String sku) { this.sku = sku; return this; }
            public ProductInsightItemBuilder category(String category) { this.category = category; return this; }
            public ProductInsightItemBuilder totalQuantitySold(Long totalQuantitySold) { this.totalQuantitySold = totalQuantitySold; return this; }
            public ProductInsightItemBuilder totalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; return this; }
            public ProductInsightItemBuilder avgDailyDemand(Double avgDailyDemand) { this.avgDailyDemand = avgDailyDemand; return this; }
            public ProductInsightItemBuilder predictedDemand(Double predictedDemand) { this.predictedDemand = predictedDemand; return this; }
            public ProductInsightItemBuilder suggestedRestock(Integer suggestedRestock) { this.suggestedRestock = suggestedRestock; return this; }
            public ProductInsightItemBuilder confidence(String confidence) { this.confidence = confidence; return this; }
            public ProductInsightItemBuilder overstockRisk(Boolean overstockRisk) { this.overstockRisk = overstockRisk; return this; }
            public ProductInsightItem build() { return new ProductInsightItem(productId, productName, sku, category, totalQuantitySold, totalRevenue, avgDailyDemand, predictedDemand, suggestedRestock, confidence, overstockRisk); }
        }

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public String getSku() { return sku; }
        public void setSku(String sku) { this.sku = sku; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public Long getTotalQuantitySold() { return totalQuantitySold; }
        public void setTotalQuantitySold(Long totalQuantitySold) { this.totalQuantitySold = totalQuantitySold; }
        public BigDecimal getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
        public Double getAvgDailyDemand() { return avgDailyDemand; }
        public void setAvgDailyDemand(Double avgDailyDemand) { this.avgDailyDemand = avgDailyDemand; }
        public Double getPredictedDemand() { return predictedDemand; }
        public void setPredictedDemand(Double predictedDemand) { this.predictedDemand = predictedDemand; }
        public Integer getSuggestedRestock() { return suggestedRestock; }
        public void setSuggestedRestock(Integer suggestedRestock) { this.suggestedRestock = suggestedRestock; }
        public String getConfidence() { return confidence; }
        public void setConfidence(String confidence) { this.confidence = confidence; }
        public Boolean getOverstockRisk() { return overstockRisk; }
        public void setOverstockRisk(Boolean overstockRisk) { this.overstockRisk = overstockRisk; }
    }
}
