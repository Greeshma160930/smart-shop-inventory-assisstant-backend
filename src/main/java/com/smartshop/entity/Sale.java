package com.smartshop.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales", indexes = {
    @Index(name = "idx_sales_product_date", columnList = "product_id, sale_date")
})
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity_sold", nullable = false)
    private Integer quantitySold;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @CreationTimestamp
    @Column(name = "sale_date", nullable = false)
    private LocalDateTime saleDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    public Sale() {}

    public Sale(Long id, Product product, Integer quantitySold, BigDecimal unitPrice, BigDecimal totalAmount, LocalDateTime saleDate, User user) {
        this.id = id;
        this.product = product;
        this.quantitySold = quantitySold;
        this.unitPrice = unitPrice;
        this.totalAmount = totalAmount;
        this.saleDate = saleDate;
        this.user = user;
    }

    public static SaleBuilder builder() {
        return new SaleBuilder();
    }

    public static class SaleBuilder {
        private Long id;
        private Product product;
        private Integer quantitySold;
        private BigDecimal unitPrice;
        private BigDecimal totalAmount;
        private LocalDateTime saleDate;
        private User user;

        public SaleBuilder id(Long id) { this.id = id; return this; }
        public SaleBuilder product(Product product) { this.product = product; return this; }
        public SaleBuilder quantitySold(Integer quantitySold) { this.quantitySold = quantitySold; return this; }
        public SaleBuilder unitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; return this; }
        public SaleBuilder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public SaleBuilder saleDate(LocalDateTime saleDate) { this.saleDate = saleDate; return this; }
        public SaleBuilder user(User user) { this.user = user; return this; }

        public Sale build() {
            return new Sale(id, product, quantitySold, unitPrice, totalAmount, saleDate, user);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantitySold() { return quantitySold; }
    public void setQuantitySold(Integer quantitySold) { this.quantitySold = quantitySold; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDateTime saleDate) { this.saleDate = saleDate; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
