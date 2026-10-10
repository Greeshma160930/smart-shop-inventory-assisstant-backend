package com.smartshop.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "demand_forecasts", indexes = {
    @Index(name = "idx_forecast_product", columnList = "product_id")
})
public class DemandForecast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "predicted_demand", nullable = false)
    private Double predictedDemand;

    @Column(name = "suggested_restock", nullable = false)
    private Integer suggestedRestock;

    @Column(nullable = false, length = 20)
    private String confidence; // Low, Medium, High

    @CreationTimestamp
    @Column(name = "forecast_date", nullable = false)
    private LocalDateTime forecastDate;

    public DemandForecast() {}

    public DemandForecast(Long id, Product product, Double predictedDemand, Integer suggestedRestock, String confidence, LocalDateTime forecastDate) {
        this.id = id;
        this.product = product;
        this.predictedDemand = predictedDemand;
        this.suggestedRestock = suggestedRestock;
        this.confidence = confidence;
        this.forecastDate = forecastDate;
    }

    public static DemandForecastBuilder builder() {
        return new DemandForecastBuilder();
    }

    public static class DemandForecastBuilder {
        private Long id;
        private Product product;
        private Double predictedDemand;
        private Integer suggestedRestock;
        private String confidence;
        private LocalDateTime forecastDate;

        public DemandForecastBuilder id(Long id) { this.id = id; return this; }
        public DemandForecastBuilder product(Product product) { this.product = product; return this; }
        public DemandForecastBuilder predictedDemand(Double predictedDemand) { this.predictedDemand = predictedDemand; return this; }
        public DemandForecastBuilder suggestedRestock(Integer suggestedRestock) { this.suggestedRestock = suggestedRestock; return this; }
        public DemandForecastBuilder confidence(String confidence) { this.confidence = confidence; return this; }
        public DemandForecastBuilder forecastDate(LocalDateTime forecastDate) { this.forecastDate = forecastDate; return this; }

        public DemandForecast build() {
            return new DemandForecast(id, product, predictedDemand, suggestedRestock, confidence, forecastDate);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Double getPredictedDemand() { return predictedDemand; }
    public void setPredictedDemand(Double predictedDemand) { this.predictedDemand = predictedDemand; }

    public Integer getSuggestedRestock() { return suggestedRestock; }
    public void setSuggestedRestock(Integer suggestedRestock) { this.suggestedRestock = suggestedRestock; }

    public String getConfidence() { return confidence; }
    public void setConfidence(String confidence) { this.confidence = confidence; }

    public LocalDateTime getForecastDate() { return forecastDate; }
    public void setForecastDate(LocalDateTime forecastDate) { this.forecastDate = forecastDate; }
}
