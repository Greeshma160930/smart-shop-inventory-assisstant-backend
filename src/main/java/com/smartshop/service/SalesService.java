package com.smartshop.service;

import com.smartshop.dto.SaleRequest;
import com.smartshop.entity.InventoryLog;
import com.smartshop.entity.Product;
import com.smartshop.entity.Sale;
import com.smartshop.entity.User;
import com.smartshop.exception.InsufficientStockException;
import com.smartshop.repository.InventoryLogRepository;
import com.smartshop.repository.ProductRepository;
import com.smartshop.repository.SaleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class SalesService {

    private static final Logger log = LoggerFactory.getLogger(SalesService.class);

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final InventoryLogRepository inventoryLogRepository;
    private final AlertService alertService;
    private final AuthService authService;

    public SalesService(SaleRepository saleRepository, ProductRepository productRepository, InventoryLogRepository inventoryLogRepository, AlertService alertService, AuthService authService) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.inventoryLogRepository = inventoryLogRepository;
        this.alertService = alertService;
        this.authService = authService;
    }

    @Transactional
    public Sale recordSale(SaleRequest request) {
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity sold must be greater than zero");
        }

        Product product = productRepository.findByIdWithLock(request.getProductId())
                .orElseGet(() -> productRepository.findById(request.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + request.getProductId())));

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new InsufficientStockException(
                    "Insufficient stock for '" + product.getName() + "'. Available: " +
                    product.getStockQuantity() + ", Requested: " + request.getQuantity()
            );
        }

        User currentUser = null;
        try {
            currentUser = authService.getCurrentUser();
        } catch (Exception e) {
            log.warn("Could not attach current user to sale: {}", e.getMessage());
        }

        BigDecimal unitPrice = product.getPrice();
        BigDecimal totalAmount = unitPrice.multiply(BigDecimal.valueOf(request.getQuantity()));

        // 1. Save Sale
        Sale sale = Sale.builder()
                .product(product)
                .quantitySold(request.getQuantity())
                .unitPrice(unitPrice)
                .totalAmount(totalAmount)
                .saleDate(LocalDateTime.now())
                .user(currentUser)
                .build();
        sale = saleRepository.save(sale);

        // 2. Reduce Product Stock (atomic deduction, protected against negative inventory)
        int resultingStock = product.getStockQuantity() - request.getQuantity();
        if (resultingStock < 0) {
            throw new InsufficientStockException("Sale would cause negative stock quantity for: " + product.getName());
        }
        product.setStockQuantity(resultingStock);
        productRepository.save(product);

        // 3. Create Inventory Log
        InventoryLog inventoryLog = InventoryLog.builder()
                .product(product)
                .changeType("SALE")
                .quantityChange(-request.getQuantity())
                .resultingStock(resultingStock)
                .logDate(LocalDateTime.now())
                .build();
        inventoryLogRepository.save(inventoryLog);

        // 4. Create / Refresh Alerts
        alertService.checkAndCreateAlerts(product);

        log.info("Recorded sale id: {}, product: {}, qty: {}, remaining stock: {}",
                sale.getId(), product.getName(), request.getQuantity(), resultingStock);

        return sale;
    }

    public Page<Sale> getSales(Long productId, LocalDate from, LocalDate to, Pageable pageable) {
        LocalDateTime fromDateTime = (from != null) ? from.atStartOfDay() : null;
        LocalDateTime toDateTime = (to != null) ? to.plusDays(1).atStartOfDay() : null;

        return saleRepository.findFilteredSales(productId, fromDateTime, toDateTime, pageable);
    }

    public List<Sale> getRecentSales() {
        return saleRepository.findTop10ByOrderBySaleDateDesc();
    }
}
