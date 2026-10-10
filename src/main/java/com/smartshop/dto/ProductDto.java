package com.smartshop.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductDto {

    private Long id;

    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    @NotNull(message = "Minimum stock level is required")
    @Min(value = 0, message = "Minimum stock level cannot be negative")
    private Integer minStockLevel;

    private String supplier;
    private String stockStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductDto() {}

    public ProductDto(Long id, String name, String sku, String category, BigDecimal price, Integer stockQuantity, Integer minStockLevel, String supplier, String stockStatus, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.category = category;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.minStockLevel = minStockLevel;
        this.supplier = supplier;
        this.stockStatus = stockStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ProductDtoBuilder builder() {
        return new ProductDtoBuilder();
    }

    public static class ProductDtoBuilder {
        private Long id;
        private String name;
        private String sku;
        private String category;
        private BigDecimal price;
        private Integer stockQuantity;
        private Integer minStockLevel;
        private String supplier;
        private String stockStatus;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public ProductDtoBuilder id(Long id) { this.id = id; return this; }
        public ProductDtoBuilder name(String name) { this.name = name; return this; }
        public ProductDtoBuilder sku(String sku) { this.sku = sku; return this; }
        public ProductDtoBuilder category(String category) { this.category = category; return this; }
        public ProductDtoBuilder price(BigDecimal price) { this.price = price; return this; }
        public ProductDtoBuilder stockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; return this; }
        public ProductDtoBuilder minStockLevel(Integer minStockLevel) { this.minStockLevel = minStockLevel; return this; }
        public ProductDtoBuilder supplier(String supplier) { this.supplier = supplier; return this; }
        public ProductDtoBuilder stockStatus(String stockStatus) { this.stockStatus = stockStatus; return this; }
        public ProductDtoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ProductDtoBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public ProductDto build() {
            return new ProductDto(id, name, sku, category, price, stockQuantity, minStockLevel, supplier, stockStatus, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public Integer getMinStockLevel() { return minStockLevel; }
    public void setMinStockLevel(Integer minStockLevel) { this.minStockLevel = minStockLevel; }

    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }

    public String getStockStatus() { return stockStatus; }
    public void setStockStatus(String stockStatus) { this.stockStatus = stockStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
