package com.smartshop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RestockRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Restock quantity must be at least 1")
    private Integer quantity;

    private String changeType = "RESTOCK"; // RESTOCK or ADJUSTMENT

    public RestockRequest() {}

    public RestockRequest(Long productId, Integer quantity, String changeType) {
        this.productId = productId;
        this.quantity = quantity;
        this.changeType = (changeType != null) ? changeType : "RESTOCK";
    }

    public static RestockRequestBuilder builder() {
        return new RestockRequestBuilder();
    }

    public static class RestockRequestBuilder {
        private Long productId;
        private Integer quantity;
        private String changeType = "RESTOCK";

        public RestockRequestBuilder productId(Long productId) { this.productId = productId; return this; }
        public RestockRequestBuilder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public RestockRequestBuilder changeType(String changeType) { this.changeType = changeType; return this; }

        public RestockRequest build() {
            return new RestockRequest(productId, quantity, changeType);
        }
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getChangeType() { return changeType; }
    public void setChangeType(String changeType) { this.changeType = changeType; }
}
