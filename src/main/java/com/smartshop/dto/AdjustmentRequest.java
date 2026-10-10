package com.smartshop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AdjustmentRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Actual physical stock is required")
    @Min(value = 0, message = "Physical stock cannot be negative")
    private Integer newPhysicalStock;

    private String reason;

    public AdjustmentRequest() {}

    public AdjustmentRequest(Long productId, Integer newPhysicalStock, String reason) {
        this.productId = productId;
        this.newPhysicalStock = newPhysicalStock;
        this.reason = reason;
    }

    public static AdjustmentRequestBuilder builder() {
        return new AdjustmentRequestBuilder();
    }

    public static class AdjustmentRequestBuilder {
        private Long productId;
        private Integer newPhysicalStock;
        private String reason;

        public AdjustmentRequestBuilder productId(Long productId) { this.productId = productId; return this; }
        public AdjustmentRequestBuilder newPhysicalStock(Integer newPhysicalStock) { this.newPhysicalStock = newPhysicalStock; return this; }
        public AdjustmentRequestBuilder reason(String reason) { this.reason = reason; return this; }

        public AdjustmentRequest build() {
            return new AdjustmentRequest(productId, newPhysicalStock, reason);
        }
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getNewPhysicalStock() { return newPhysicalStock; }
    public void setNewPhysicalStock(Integer newPhysicalStock) { this.newPhysicalStock = newPhysicalStock; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
