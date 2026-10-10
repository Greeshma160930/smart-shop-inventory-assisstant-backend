package com.smartshop.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_logs", indexes = {
    @Index(name = "idx_inventory_logs_product", columnList = "product_id")
})
public class InventoryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "change_type", nullable = false, length = 20)
    private String changeType; // SALE, RESTOCK, ADJUSTMENT

    @Column(name = "quantity_change", nullable = false)
    private Integer quantityChange;

    @Column(name = "resulting_stock", nullable = false)
    private Integer resultingStock;

    @CreationTimestamp
    @Column(name = "log_date", nullable = false)
    private LocalDateTime logDate;

    public InventoryLog() {}

    public InventoryLog(Long id, Product product, String changeType, Integer quantityChange, Integer resultingStock, LocalDateTime logDate) {
        this.id = id;
        this.product = product;
        this.changeType = changeType;
        this.quantityChange = quantityChange;
        this.resultingStock = resultingStock;
        this.logDate = logDate;
    }

    public static InventoryLogBuilder builder() {
        return new InventoryLogBuilder();
    }

    public static class InventoryLogBuilder {
        private Long id;
        private Product product;
        private String changeType;
        private Integer quantityChange;
        private Integer resultingStock;
        private LocalDateTime logDate;

        public InventoryLogBuilder id(Long id) { this.id = id; return this; }
        public InventoryLogBuilder product(Product product) { this.product = product; return this; }
        public InventoryLogBuilder changeType(String changeType) { this.changeType = changeType; return this; }
        public InventoryLogBuilder quantityChange(Integer quantityChange) { this.quantityChange = quantityChange; return this; }
        public InventoryLogBuilder resultingStock(Integer resultingStock) { this.resultingStock = resultingStock; return this; }
        public InventoryLogBuilder logDate(LocalDateTime logDate) { this.logDate = logDate; return this; }

        public InventoryLog build() {
            return new InventoryLog(id, product, changeType, quantityChange, resultingStock, logDate);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public String getChangeType() { return changeType; }
    public void setChangeType(String changeType) { this.changeType = changeType; }

    public Integer getQuantityChange() { return quantityChange; }
    public void setQuantityChange(Integer quantityChange) { this.quantityChange = quantityChange; }

    public Integer getResultingStock() { return resultingStock; }
    public void setResultingStock(Integer resultingStock) { this.resultingStock = resultingStock; }

    public LocalDateTime getLogDate() { return logDate; }
    public void setLogDate(LocalDateTime logDate) { this.logDate = logDate; }
}
