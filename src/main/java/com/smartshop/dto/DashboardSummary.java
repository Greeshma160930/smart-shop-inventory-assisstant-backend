package com.smartshop.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DashboardSummary {

    private Long totalProducts;
    private BigDecimal totalSalesToday;
    private Long lowStockCount;
    private Long outOfStockCount;
    private Double predictedDemandNext7Days;
    private Double salesTodayGrowth;

    private List<SalesTrendPoint> salesTrend = new ArrayList<>();
    private List<CategoryStockPoint> categoryStock = new ArrayList<>();
    private StockLevelDistribution stockLevels;

    private List<ForecastResponse> restockingSuggestions = new ArrayList<>();
    private List<RecentSaleDto> recentSales = new ArrayList<>();
    private List<AlertDto> activeAlerts = new ArrayList<>();

    public DashboardSummary() {}

    public DashboardSummary(Long totalProducts, BigDecimal totalSalesToday, Long lowStockCount, Long outOfStockCount, Double predictedDemandNext7Days, Double salesTodayGrowth, List<SalesTrendPoint> salesTrend, List<CategoryStockPoint> categoryStock, StockLevelDistribution stockLevels, List<ForecastResponse> restockingSuggestions, List<RecentSaleDto> recentSales, List<AlertDto> activeAlerts) {
        this.totalProducts = totalProducts;
        this.totalSalesToday = totalSalesToday;
        this.lowStockCount = lowStockCount;
        this.outOfStockCount = outOfStockCount;
        this.predictedDemandNext7Days = predictedDemandNext7Days;
        this.salesTodayGrowth = salesTodayGrowth;
        this.salesTrend = (salesTrend != null) ? salesTrend : new ArrayList<>();
        this.categoryStock = (categoryStock != null) ? categoryStock : new ArrayList<>();
        this.stockLevels = stockLevels;
        this.restockingSuggestions = (restockingSuggestions != null) ? restockingSuggestions : new ArrayList<>();
        this.recentSales = (recentSales != null) ? recentSales : new ArrayList<>();
        this.activeAlerts = (activeAlerts != null) ? activeAlerts : new ArrayList<>();
    }

    public static DashboardSummaryBuilder builder() {
        return new DashboardSummaryBuilder();
    }

    public static class DashboardSummaryBuilder {
        private Long totalProducts;
        private BigDecimal totalSalesToday;
        private Long lowStockCount;
        private Long outOfStockCount;
        private Double predictedDemandNext7Days;
        private Double salesTodayGrowth;
        private List<SalesTrendPoint> salesTrend = new ArrayList<>();
        private List<CategoryStockPoint> categoryStock = new ArrayList<>();
        private StockLevelDistribution stockLevels;
        private List<ForecastResponse> restockingSuggestions = new ArrayList<>();
        private List<RecentSaleDto> recentSales = new ArrayList<>();
        private List<AlertDto> activeAlerts = new ArrayList<>();

        public DashboardSummaryBuilder totalProducts(Long totalProducts) { this.totalProducts = totalProducts; return this; }
        public DashboardSummaryBuilder totalSalesToday(BigDecimal totalSalesToday) { this.totalSalesToday = totalSalesToday; return this; }
        public DashboardSummaryBuilder lowStockCount(Long lowStockCount) { this.lowStockCount = lowStockCount; return this; }
        public DashboardSummaryBuilder outOfStockCount(Long outOfStockCount) { this.outOfStockCount = outOfStockCount; return this; }
        public DashboardSummaryBuilder predictedDemandNext7Days(Double predictedDemandNext7Days) { this.predictedDemandNext7Days = predictedDemandNext7Days; return this; }
        public DashboardSummaryBuilder salesTodayGrowth(Double salesTodayGrowth) { this.salesTodayGrowth = salesTodayGrowth; return this; }
        public DashboardSummaryBuilder salesTrend(List<SalesTrendPoint> salesTrend) { this.salesTrend = salesTrend; return this; }
        public DashboardSummaryBuilder categoryStock(List<CategoryStockPoint> categoryStock) { this.categoryStock = categoryStock; return this; }
        public DashboardSummaryBuilder stockLevels(StockLevelDistribution stockLevels) { this.stockLevels = stockLevels; return this; }
        public DashboardSummaryBuilder restockingSuggestions(List<ForecastResponse> restockingSuggestions) { this.restockingSuggestions = restockingSuggestions; return this; }
        public DashboardSummaryBuilder recentSales(List<RecentSaleDto> recentSales) { this.recentSales = recentSales; return this; }
        public DashboardSummaryBuilder activeAlerts(List<AlertDto> activeAlerts) { this.activeAlerts = activeAlerts; return this; }

        public DashboardSummary build() {
            return new DashboardSummary(totalProducts, totalSalesToday, lowStockCount, outOfStockCount, predictedDemandNext7Days, salesTodayGrowth, salesTrend, categoryStock, stockLevels, restockingSuggestions, recentSales, activeAlerts);
        }
    }

    public static class SalesTrendPoint {
        private String date;
        private BigDecimal revenue;
        private Long quantitySold;

        public SalesTrendPoint() {}
        public SalesTrendPoint(String date, BigDecimal revenue, Long quantitySold) {
            this.date = date;
            this.revenue = revenue;
            this.quantitySold = quantitySold;
        }

        public static SalesTrendPointBuilder builder() { return new SalesTrendPointBuilder(); }
        public static class SalesTrendPointBuilder {
            private String date;
            private BigDecimal revenue;
            private Long quantitySold;
            public SalesTrendPointBuilder date(String date) { this.date = date; return this; }
            public SalesTrendPointBuilder revenue(BigDecimal revenue) { this.revenue = revenue; return this; }
            public SalesTrendPointBuilder quantitySold(Long quantitySold) { this.quantitySold = quantitySold; return this; }
            public SalesTrendPoint build() { return new SalesTrendPoint(date, revenue, quantitySold); }
        }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
        public Long getQuantitySold() { return quantitySold; }
        public void setQuantitySold(Long quantitySold) { this.quantitySold = quantitySold; }
    }

    public static class CategoryStockPoint {
        private String category;
        private Long productCount;
        private Long totalStock;
        private BigDecimal totalValue;

        public CategoryStockPoint() {}
        public CategoryStockPoint(String category, Long productCount, Long totalStock, BigDecimal totalValue) {
            this.category = category;
            this.productCount = productCount;
            this.totalStock = totalStock;
            this.totalValue = totalValue;
        }

        public static CategoryStockPointBuilder builder() { return new CategoryStockPointBuilder(); }
        public static class CategoryStockPointBuilder {
            private String category;
            private Long productCount;
            private Long totalStock;
            private BigDecimal totalValue;
            public CategoryStockPointBuilder category(String category) { this.category = category; return this; }
            public CategoryStockPointBuilder productCount(Long productCount) { this.productCount = productCount; return this; }
            public CategoryStockPointBuilder totalStock(Long totalStock) { this.totalStock = totalStock; return this; }
            public CategoryStockPointBuilder totalValue(BigDecimal totalValue) { this.totalValue = totalValue; return this; }
            public CategoryStockPoint build() { return new CategoryStockPoint(category, productCount, totalStock, totalValue); }
        }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public Long getProductCount() { return productCount; }
        public void setProductCount(Long productCount) { this.productCount = productCount; }
        public Long getTotalStock() { return totalStock; }
        public void setTotalStock(Long totalStock) { this.totalStock = totalStock; }
        public BigDecimal getTotalValue() { return totalValue; }
        public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
    }

    public static class StockLevelDistribution {
        private Long inStock;
        private Long lowStock;
        private Long outOfStock;

        public StockLevelDistribution() {}
        public StockLevelDistribution(Long inStock, Long lowStock, Long outOfStock) {
            this.inStock = inStock;
            this.lowStock = lowStock;
            this.outOfStock = outOfStock;
        }

        public static StockLevelDistributionBuilder builder() { return new StockLevelDistributionBuilder(); }
        public static class StockLevelDistributionBuilder {
            private Long inStock;
            private Long lowStock;
            private Long outOfStock;
            public StockLevelDistributionBuilder inStock(Long inStock) { this.inStock = inStock; return this; }
            public StockLevelDistributionBuilder lowStock(Long lowStock) { this.lowStock = lowStock; return this; }
            public StockLevelDistributionBuilder outOfStock(Long outOfStock) { this.outOfStock = outOfStock; return this; }
            public StockLevelDistribution build() { return new StockLevelDistribution(inStock, lowStock, outOfStock); }
        }

        public Long getInStock() { return inStock; }
        public void setInStock(Long inStock) { this.inStock = inStock; }
        public Long getLowStock() { return lowStock; }
        public void setLowStock(Long lowStock) { this.lowStock = lowStock; }
        public Long getOutOfStock() { return outOfStock; }
        public void setOutOfStock(Long outOfStock) { this.outOfStock = outOfStock; }
    }

    public static class RecentSaleDto {
        private Long id;
        private String productName;
        private String sku;
        private Integer quantitySold;
        private BigDecimal unitPrice;
        private BigDecimal totalAmount;
        private String saleDate;
        private String userName;

        public RecentSaleDto() {}
        public RecentSaleDto(Long id, String productName, String sku, Integer quantitySold, BigDecimal unitPrice, BigDecimal totalAmount, String saleDate, String userName) {
            this.id = id;
            this.productName = productName;
            this.sku = sku;
            this.quantitySold = quantitySold;
            this.unitPrice = unitPrice;
            this.totalAmount = totalAmount;
            this.saleDate = saleDate;
            this.userName = userName;
        }

        public static RecentSaleDtoBuilder builder() { return new RecentSaleDtoBuilder(); }
        public static class RecentSaleDtoBuilder {
            private Long id;
            private String productName;
            private String sku;
            private Integer quantitySold;
            private BigDecimal unitPrice;
            private BigDecimal totalAmount;
            private String saleDate;
            private String userName;
            public RecentSaleDtoBuilder id(Long id) { this.id = id; return this; }
            public RecentSaleDtoBuilder productName(String productName) { this.productName = productName; return this; }
            public RecentSaleDtoBuilder sku(String sku) { this.sku = sku; return this; }
            public RecentSaleDtoBuilder quantitySold(Integer quantitySold) { this.quantitySold = quantitySold; return this; }
            public RecentSaleDtoBuilder unitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; return this; }
            public RecentSaleDtoBuilder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
            public RecentSaleDtoBuilder saleDate(String saleDate) { this.saleDate = saleDate; return this; }
            public RecentSaleDtoBuilder userName(String userName) { this.userName = userName; return this; }
            public RecentSaleDto build() { return new RecentSaleDto(id, productName, sku, quantitySold, unitPrice, totalAmount, saleDate, userName); }
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public String getSku() { return sku; }
        public void setSku(String sku) { this.sku = sku; }
        public Integer getQuantitySold() { return quantitySold; }
        public void setQuantitySold(Integer quantitySold) { this.quantitySold = quantitySold; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        public String getSaleDate() { return saleDate; }
        public void setSaleDate(String saleDate) { this.saleDate = saleDate; }
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
    }

    public static class AlertDto {
        private Long id;
        private Long productId;
        private String productName;
        private String sku;
        private String type;
        private String message;
        private Boolean isRead;
        private String createdAt;

        public AlertDto() {}
        public AlertDto(Long id, Long productId, String productName, String sku, String type, String message, Boolean isRead, String createdAt) {
            this.id = id;
            this.productId = productId;
            this.productName = productName;
            this.sku = sku;
            this.type = type;
            this.message = message;
            this.isRead = isRead;
            this.createdAt = createdAt;
        }

        public static AlertDtoBuilder builder() { return new AlertDtoBuilder(); }
        public static class AlertDtoBuilder {
            private Long id;
            private Long productId;
            private String productName;
            private String sku;
            private String type;
            private String message;
            private Boolean isRead;
            private String createdAt;
            public AlertDtoBuilder id(Long id) { this.id = id; return this; }
            public AlertDtoBuilder productId(Long productId) { this.productId = productId; return this; }
            public AlertDtoBuilder productName(String productName) { this.productName = productName; return this; }
            public AlertDtoBuilder sku(String sku) { this.sku = sku; return this; }
            public AlertDtoBuilder type(String type) { this.type = type; return this; }
            public AlertDtoBuilder message(String message) { this.message = message; return this; }
            public AlertDtoBuilder isRead(Boolean isRead) { this.isRead = isRead; return this; }
            public AlertDtoBuilder createdAt(String createdAt) { this.createdAt = createdAt; return this; }
            public AlertDto build() { return new AlertDto(id, productId, productName, sku, type, message, isRead, createdAt); }
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public String getSku() { return sku; }
        public void setSku(String sku) { this.sku = sku; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Boolean getIsRead() { return isRead; }
        public void setIsRead(Boolean isRead) { this.isRead = isRead; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    }

    public Long getTotalProducts() { return totalProducts; }
    public void setTotalProducts(Long totalProducts) { this.totalProducts = totalProducts; }
    public BigDecimal getTotalSalesToday() { return totalSalesToday; }
    public void setTotalSalesToday(BigDecimal totalSalesToday) { this.totalSalesToday = totalSalesToday; }
    public Long getLowStockCount() { return lowStockCount; }
    public void setLowStockCount(Long lowStockCount) { this.lowStockCount = lowStockCount; }
    public Long getOutOfStockCount() { return outOfStockCount; }
    public void setOutOfStockCount(Long outOfStockCount) { this.outOfStockCount = outOfStockCount; }
    public Double getPredictedDemandNext7Days() { return predictedDemandNext7Days; }
    public void setPredictedDemandNext7Days(Double predictedDemandNext7Days) { this.predictedDemandNext7Days = predictedDemandNext7Days; }
    public Double getSalesTodayGrowth() { return salesTodayGrowth; }
    public void setSalesTodayGrowth(Double salesTodayGrowth) { this.salesTodayGrowth = salesTodayGrowth; }
    public List<SalesTrendPoint> getSalesTrend() { return salesTrend; }
    public void setSalesTrend(List<SalesTrendPoint> salesTrend) { this.salesTrend = salesTrend; }
    public List<CategoryStockPoint> getCategoryStock() { return categoryStock; }
    public void setCategoryStock(List<CategoryStockPoint> categoryStock) { this.categoryStock = categoryStock; }
    public StockLevelDistribution getStockLevels() { return stockLevels; }
    public void setStockLevels(StockLevelDistribution stockLevels) { this.stockLevels = stockLevels; }
    public List<ForecastResponse> getRestockingSuggestions() { return restockingSuggestions; }
    public void setRestockingSuggestions(List<ForecastResponse> restockingSuggestions) { this.restockingSuggestions = restockingSuggestions; }
    public List<RecentSaleDto> getRecentSales() { return recentSales; }
    public void setRecentSales(List<RecentSaleDto> recentSales) { this.recentSales = recentSales; }
    public List<AlertDto> getActiveAlerts() { return activeAlerts; }
    public void setActiveAlerts(List<AlertDto> activeAlerts) { this.activeAlerts = activeAlerts; }
}
