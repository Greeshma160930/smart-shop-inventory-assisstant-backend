package com.smartshop.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ForecastResponse {

    private Long productId;
    private String productName;
    private String sku;
    private String category;
    private Integer currentStock;
    private Integer minStockLevel;
    private Double predictedDemand;
    private Integer suggestedRestock;
    private String confidence;
    private Boolean overstockRisk;
    private Double regressionProjection;
    private Double movingAverageProjection;
    private Integer historicalSalesCount;
    private LocalDateTime forecastDate;

    private List<DailySalesPoint> historicalPoints = new ArrayList<>();
    private List<DailyForecastPoint> forecastPoints = new ArrayList<>();

    public ForecastResponse() {}

    public ForecastResponse(Long productId, String productName, String sku, String category, Integer currentStock, Integer minStockLevel, Double predictedDemand, Integer suggestedRestock, String confidence, Boolean overstockRisk, Double regressionProjection, Double movingAverageProjection, Integer historicalSalesCount, LocalDateTime forecastDate, List<DailySalesPoint> historicalPoints, List<DailyForecastPoint> forecastPoints) {
        this.productId = productId;
        this.productName = productName;
        this.sku = sku;
        this.category = category;
        this.currentStock = currentStock;
        this.minStockLevel = minStockLevel;
        this.predictedDemand = predictedDemand;
        this.suggestedRestock = suggestedRestock;
        this.confidence = confidence;
        this.overstockRisk = overstockRisk;
        this.regressionProjection = regressionProjection;
        this.movingAverageProjection = movingAverageProjection;
        this.historicalSalesCount = historicalSalesCount;
        this.forecastDate = forecastDate;
        this.historicalPoints = (historicalPoints != null) ? historicalPoints : new ArrayList<>();
        this.forecastPoints = (forecastPoints != null) ? forecastPoints : new ArrayList<>();
    }

    public static ForecastResponseBuilder builder() {
        return new ForecastResponseBuilder();
    }

    public static class ForecastResponseBuilder {
        private Long productId;
        private String productName;
        private String sku;
        private String category;
        private Integer currentStock;
        private Integer minStockLevel;
        private Double predictedDemand;
        private Integer suggestedRestock;
        private String confidence;
        private Boolean overstockRisk;
        private Double regressionProjection;
        private Double movingAverageProjection;
        private Integer historicalSalesCount;
        private LocalDateTime forecastDate;
        private List<DailySalesPoint> historicalPoints = new ArrayList<>();
        private List<DailyForecastPoint> forecastPoints = new ArrayList<>();

        public ForecastResponseBuilder productId(Long productId) { this.productId = productId; return this; }
        public ForecastResponseBuilder productName(String productName) { this.productName = productName; return this; }
        public ForecastResponseBuilder sku(String sku) { this.sku = sku; return this; }
        public ForecastResponseBuilder category(String category) { this.category = category; return this; }
        public ForecastResponseBuilder currentStock(Integer currentStock) { this.currentStock = currentStock; return this; }
        public ForecastResponseBuilder minStockLevel(Integer minStockLevel) { this.minStockLevel = minStockLevel; return this; }
        public ForecastResponseBuilder predictedDemand(Double predictedDemand) { this.predictedDemand = predictedDemand; return this; }
        public ForecastResponseBuilder suggestedRestock(Integer suggestedRestock) { this.suggestedRestock = suggestedRestock; return this; }
        public ForecastResponseBuilder confidence(String confidence) { this.confidence = confidence; return this; }
        public ForecastResponseBuilder overstockRisk(Boolean overstockRisk) { this.overstockRisk = overstockRisk; return this; }
        public ForecastResponseBuilder regressionProjection(Double regressionProjection) { this.regressionProjection = regressionProjection; return this; }
        public ForecastResponseBuilder movingAverageProjection(Double movingAverageProjection) { this.movingAverageProjection = movingAverageProjection; return this; }
        public ForecastResponseBuilder historicalSalesCount(Integer historicalSalesCount) { this.historicalSalesCount = historicalSalesCount; return this; }
        public ForecastResponseBuilder forecastDate(LocalDateTime forecastDate) { this.forecastDate = forecastDate; return this; }
        public ForecastResponseBuilder historicalPoints(List<DailySalesPoint> historicalPoints) { this.historicalPoints = historicalPoints; return this; }
        public ForecastResponseBuilder forecastPoints(List<DailyForecastPoint> forecastPoints) { this.forecastPoints = forecastPoints; return this; }

        public ForecastResponse build() {
            return new ForecastResponse(productId, productName, sku, category, currentStock, minStockLevel, predictedDemand, suggestedRestock, confidence, overstockRisk, regressionProjection, movingAverageProjection, historicalSalesCount, forecastDate, historicalPoints, forecastPoints);
        }
    }

    public static class DailySalesPoint {
        private String date;
        private Double quantity;

        public DailySalesPoint() {}
        public DailySalesPoint(String date, Double quantity) {
            this.date = date;
            this.quantity = quantity;
        }

        public static DailySalesPointBuilder builder() { return new DailySalesPointBuilder(); }
        public static class DailySalesPointBuilder {
            private String date;
            private Double quantity;
            public DailySalesPointBuilder date(String date) { this.date = date; return this; }
            public DailySalesPointBuilder quantity(Double quantity) { this.quantity = quantity; return this; }
            public DailySalesPoint build() { return new DailySalesPoint(date, quantity); }
        }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public Double getQuantity() { return quantity; }
        public void setQuantity(Double quantity) { this.quantity = quantity; }
    }

    public static class DailyForecastPoint {
        private String date;
        private Double predictedQuantity;

        public DailyForecastPoint() {}
        public DailyForecastPoint(String date, Double predictedQuantity) {
            this.date = date;
            this.predictedQuantity = predictedQuantity;
        }

        public static DailyForecastPointBuilder builder() { return new DailyForecastPointBuilder(); }
        public static class DailyForecastPointBuilder {
            private String date;
            private Double predictedQuantity;
            public DailyForecastPointBuilder date(String date) { this.date = date; return this; }
            public DailyForecastPointBuilder predictedQuantity(Double predictedQuantity) { this.predictedQuantity = predictedQuantity; return this; }
            public DailyForecastPoint build() { return new DailyForecastPoint(date, predictedQuantity); }
        }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public Double getPredictedQuantity() { return predictedQuantity; }
        public void setPredictedQuantity(Double predictedQuantity) { this.predictedQuantity = predictedQuantity; }
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
    public Double getPredictedDemand() { return predictedDemand; }
    public void setPredictedDemand(Double predictedDemand) { this.predictedDemand = predictedDemand; }
    public Integer getSuggestedRestock() { return suggestedRestock; }
    public void setSuggestedRestock(Integer suggestedRestock) { this.suggestedRestock = suggestedRestock; }
    public String getConfidence() { return confidence; }
    public void setConfidence(String confidence) { this.confidence = confidence; }
    public Boolean getOverstockRisk() { return overstockRisk; }
    public void setOverstockRisk(Boolean overstockRisk) { this.overstockRisk = overstockRisk; }
    public Double getRegressionProjection() { return regressionProjection; }
    public void setRegressionProjection(Double regressionProjection) { this.regressionProjection = regressionProjection; }
    public Double getMovingAverageProjection() { return movingAverageProjection; }
    public void setMovingAverageProjection(Double movingAverageProjection) { this.movingAverageProjection = movingAverageProjection; }
    public Integer getHistoricalSalesCount() { return historicalSalesCount; }
    public void setHistoricalSalesCount(Integer historicalSalesCount) { this.historicalSalesCount = historicalSalesCount; }
    public LocalDateTime getForecastDate() { return forecastDate; }
    public void setForecastDate(LocalDateTime forecastDate) { this.forecastDate = forecastDate; }
    public List<DailySalesPoint> getHistoricalPoints() { return historicalPoints; }
    public void setHistoricalPoints(List<DailySalesPoint> historicalPoints) { this.historicalPoints = historicalPoints; }
    public List<DailyForecastPoint> getForecastPoints() { return forecastPoints; }
    public void setForecastPoints(List<DailyForecastPoint> forecastPoints) { this.forecastPoints = forecastPoints; }
}
