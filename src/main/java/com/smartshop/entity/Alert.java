package com.smartshop.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerts", indexes = {
    @Index(name = "idx_alerts_product", columnList = "product_id"),
    @Index(name = "idx_alerts_read", columnList = "is_read")
})
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, length = 30)
    private String type; // LOW_STOCK, OUT_OF_STOCK

    @Column(nullable = false, length = 255)
    private String message;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Alert() {}

    public Alert(Long id, Product product, String type, String message, Boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.product = product;
        this.type = type;
        this.message = message;
        this.isRead = (isRead != null) ? isRead : false;
        this.createdAt = createdAt;
    }

    public static AlertBuilder builder() {
        return new AlertBuilder();
    }

    public static class AlertBuilder {
        private Long id;
        private Product product;
        private String type;
        private String message;
        private Boolean isRead = false;
        private LocalDateTime createdAt;

        public AlertBuilder id(Long id) { this.id = id; return this; }
        public AlertBuilder product(Product product) { this.product = product; return this; }
        public AlertBuilder type(String type) { this.type = type; return this; }
        public AlertBuilder message(String message) { this.message = message; return this; }
        public AlertBuilder isRead(Boolean isRead) { this.isRead = isRead; return this; }
        public AlertBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Alert build() {
            return new Alert(id, product, type, message, isRead, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
