package com.smartshop.service;

import com.smartshop.dto.AdjustmentRequest;
import com.smartshop.dto.RestockRequest;
import com.smartshop.entity.InventoryLog;
import com.smartshop.entity.Product;
import com.smartshop.exception.ResourceNotFoundException;
import com.smartshop.repository.AlertRepository;
import com.smartshop.repository.InventoryLogRepository;
import com.smartshop.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    private final ProductRepository productRepository;
    private final InventoryLogRepository inventoryLogRepository;
    private final AlertRepository alertRepository;
    private final AlertService alertService;

    public InventoryService(ProductRepository productRepository, InventoryLogRepository inventoryLogRepository, AlertRepository alertRepository, AlertService alertService) {
        this.productRepository = productRepository;
        this.inventoryLogRepository = inventoryLogRepository;
        this.alertRepository = alertRepository;
        this.alertService = alertService;
    }

    @Transactional
    public InventoryLog restockProduct(RestockRequest request) {
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Restock quantity must be greater than zero");
        }

        Product product = productRepository.findByIdWithLock(request.getProductId())
                .orElseGet(() -> productRepository.findById(request.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId())));

        int resultingStock = product.getStockQuantity() + request.getQuantity();
        product.setStockQuantity(resultingStock);
        productRepository.save(product);

        InventoryLog logEntry = InventoryLog.builder()
                .product(product)
                .changeType("RESTOCK")
                .quantityChange(request.getQuantity())
                .resultingStock(resultingStock)
                .logDate(LocalDateTime.now())
                .build();
        logEntry = inventoryLogRepository.save(logEntry);

        // Refresh/resolve alerts
        alertService.checkAndCreateAlerts(product);

        log.info("Restocked product {} ('{}') with qty +{}, new stock: {}",
                product.getId(), product.getName(), request.getQuantity(), resultingStock);
        return logEntry;
    }

    @Transactional
    public InventoryLog adjustProduct(AdjustmentRequest request) {
        if (request.getNewPhysicalStock() == null || request.getNewPhysicalStock() < 0) {
            throw new IllegalArgumentException("Physical stock count cannot be negative");
        }

        Product product = productRepository.findByIdWithLock(request.getProductId())
                .orElseGet(() -> productRepository.findById(request.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId())));

        int previousStock = product.getStockQuantity();
        int newStock = request.getNewPhysicalStock();
        int difference = newStock - previousStock;

        product.setStockQuantity(newStock);
        productRepository.save(product);

        InventoryLog logEntry = InventoryLog.builder()
                .product(product)
                .changeType("ADJUSTMENT")
                .quantityChange(difference)
                .resultingStock(newStock)
                .logDate(LocalDateTime.now())
                .build();
        logEntry = inventoryLogRepository.save(logEntry);

        // Refresh/resolve alerts
        alertService.checkAndCreateAlerts(product);

        log.info("Adjusted stock for product {} ('{}') from {} to {} (diff: {}), reason: {}",
                product.getId(), product.getName(), previousStock, newStock, difference,
                request.getReason() != null ? request.getReason() : "Physical count audit");
        return logEntry;
    }

    public Page<InventoryLog> getInventoryLogs(Long productId, Pageable pageable) {
        return inventoryLogRepository.findFilteredLogs(productId, pageable);
    }
}
