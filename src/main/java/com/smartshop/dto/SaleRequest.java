package com.smartshop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class SaleRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be greater than zero")
    private Integer quantity;

    public SaleRequest() {}

    public SaleRequest(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public static SaleRequestBuilder builder() {
        return new SaleRequestBuilder();
    }

    public static class SaleRequestBuilder {
        private Long productId;
        private Integer quantity;

        public SaleRequestBuilder productId(Long productId) { this.productId = productId; return this; }
        public SaleRequestBuilder quantity(Integer quantity) { this.quantity = quantity; return this; }

        public SaleRequest build() {
            return new SaleRequest(productId, quantity);
        }
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
