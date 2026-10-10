package com.smartshop.service;

import com.smartshop.entity.Alert;
import com.smartshop.entity.Product;
import com.smartshop.exception.ResourceNotFoundException;
import com.smartshop.repository.AlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AlertService {

    private static final Logger log = LoggerFactory.getLogger(AlertService.class);

    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @Transactional
    public void checkAndCreateAlerts(Product product) {
        int stock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
        int minStock = product.getMinStockLevel() != null ? product.getMinStockLevel() : 10;

        if (stock <= 0) {
            // OUT OF STOCK: mark any existing LOW_STOCK alerts as read/resolved
            List<Alert> lowStockAlerts = alertRepository.findByProductAndTypeAndIsReadFalse(product, "LOW_STOCK");
            if (!lowStockAlerts.isEmpty()) {
                lowStockAlerts.forEach(a -> a.setIsRead(true));
                alertRepository.saveAll(lowStockAlerts);
            }

            Optional<Alert> existing = alertRepository.findFirstByProductAndTypeAndIsReadFalse(product, "OUT_OF_STOCK");
            if (existing.isEmpty()) {
                Alert alert = Alert.builder()
                        .product(product)
                        .type("OUT_OF_STOCK")
                        .message("CRITICAL: Product '" + product.getName() + "' is out of stock (Stock: 0). Immediate restock required!")
                        .isRead(false)
                        .build();
                alertRepository.save(alert);
                log.info("Created OUT_OF_STOCK alert for product {}", product.getId());
            }
        } else if (stock <= minStock) {
            // LOW STOCK: mark any existing OUT_OF_STOCK alerts as read/resolved
            List<Alert> outOfStockAlerts = alertRepository.findByProductAndTypeAndIsReadFalse(product, "OUT_OF_STOCK");
            if (!outOfStockAlerts.isEmpty()) {
                outOfStockAlerts.forEach(a -> a.setIsRead(true));
                alertRepository.saveAll(outOfStockAlerts);
            }

            Optional<Alert> existing = alertRepository.findFirstByProductAndTypeAndIsReadFalse(product, "LOW_STOCK");
            if (existing.isEmpty()) {
                Alert alert = Alert.builder()
                        .product(product)
                        .type("LOW_STOCK")
                        .message("WARNING: Product '" + product.getName() + "' is low on stock (" + stock + " remaining, threshold: " + minStock + ").")
                        .isRead(false)
                        .build();
                alertRepository.save(alert);
                log.info("Created LOW_STOCK alert for product {}", product.getId());
            }
        } else {
            // NORMAL HEALTHY STOCK: resolve all unread alerts for this product
            List<Alert> activeAlerts = alertRepository.findByProductAndIsReadFalse(product);
            if (!activeAlerts.isEmpty()) {
                activeAlerts.forEach(a -> a.setIsRead(true));
                alertRepository.saveAll(activeAlerts);
                log.info("Resolved {} active alerts for product {} (stock now healthy: {})",
                        activeAlerts.size(), product.getId(), stock);
            }
        }
    }

    public List<Alert> getAllAlerts() {
        return alertRepository.findAllByOrderByCreatedAtDesc();
    }

    public Page<Alert> getAlerts(Boolean unreadOnly, Pageable pageable) {
        if (Boolean.TRUE.equals(unreadOnly)) {
            return alertRepository.findByIsReadFalseOrderByCreatedAtDesc(pageable);
        }
        return alertRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public List<Alert> getUnreadAlerts() {
        return alertRepository.findByIsReadFalseOrderByCreatedAtDesc();
    }

    public long getUnreadCount() {
        return alertRepository.countByIsReadFalse();
    }

    @Transactional
    public Alert markAsRead(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found with id: " + alertId));
        alert.setIsRead(true);
        return alertRepository.save(alert);
    }

    @Transactional
    public void markAllAsRead() {
        List<Alert> unread = alertRepository.findByIsReadFalseOrderByCreatedAtDesc();
        for (Alert a : unread) {
            a.setIsRead(true);
        }
        alertRepository.saveAll(unread);
    }
}
