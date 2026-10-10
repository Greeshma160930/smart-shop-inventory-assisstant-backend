package com.smartshop.service;

import com.smartshop.entity.*;
import com.smartshop.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Component
@Order(10)
public class DemoDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);

    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;
    private final InventoryLogRepository inventoryLogRepository;
    private final UserRepository userRepository;
    private final AlertService alertService;
    private final ForecastService forecastService;

    public DemoDataSeeder(ProductRepository productRepository,
                          SaleRepository saleRepository,
                          InventoryLogRepository inventoryLogRepository,
                          UserRepository userRepository,
                          AlertService alertService,
                          ForecastService forecastService) {
        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
        this.inventoryLogRepository = inventoryLogRepository;
        this.userRepository = userRepository;
        this.alertService = alertService;
        this.forecastService = forecastService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (productRepository.count() > 0) {
            log.info("Product catalog already initialized ({} products present). Skipping demo seeding.",
                    productRepository.count());
            return;
        }

        log.info("Product catalog is empty. Initializing demo products and historical sales for store...");

        User demoUser = userRepository.findByEmail("demo@smartshop.com").orElse(null);

        // 1. Create 12 Core Store Products
        List<Product> products = new ArrayList<>();
        products.add(createProduct("Basmati Rice 5kg", "SKU-RICE-001", "Food Grains", 550.00, 45, 20, "Agro Farms India"));
        products.add(createProduct("Sunflower Cooking Oil 1L", "SKU-OIL-002", "Oils & Ghee", 175.00, 35, 15, "Fortune Foods Ltd"));
        products.add(createProduct("Refined Sugar 1kg", "SKU-SUGAR-003", "Essentials", 44.00, 0, 25, "Madhur Sugars Ltd"));
        products.add(createProduct("Premium Assam Tea 500g", "SKU-TEA-004", "Beverages", 280.00, 8, 15, "Tata Tea Distributors"));
        products.add(createProduct("Bathing Soap 4-Pack", "SKU-SOAP-005", "Personal Care", 145.00, 12, 20, "Hindustan Consumer Care"));
        products.add(createProduct("Digestive Biscuits 200g", "SKU-BISC-006", "Snacks & Bakery", 35.00, 65, 20, "Britannia Wholesale"));
        products.add(createProduct("Ruled Notebooks (Pack of 3)", "SKU-NOTE-007", "Stationery", 120.00, 50, 15, "Classmate Supplies"));
        products.add(createProduct("Ballpoint Pens (Pack of 10)", "SKU-PENS-008", "Stationery", 80.00, 80, 20, "Reynolds Hub"));
        products.add(createProduct("Herbal Toothpaste 150g", "SKU-TP-009", "Personal Care", 95.00, 40, 15, "Dabur India Distribution"));
        products.add(createProduct("Anti-Dandruff Shampoo 200ml", "SKU-SHAMP-010", "Personal Care", 210.00, 30, 10, "Hindustan Consumer Care"));
        products.add(createProduct("Washing Detergent 1kg", "SKU-DET-011", "Household & Cleaning", 160.00, 55, 15, "Surf Excel Depot"));
        products.add(createProduct("Dairy Milk Powder 500g", "SKU-MILK-012", "Dairy & Beverages", 260.00, 28, 12, "Amul Cooperative"));

        products = productRepository.saveAll(products);

        // 2. Initial Inventory Movement Logs
        List<InventoryLog> logs = new ArrayList<>();
        for (Product p : products) {
            logs.add(InventoryLog.builder()
                    .product(p)
                    .changeType("RESTOCK")
                    .quantityChange(p.getStockQuantity() + 50)
                    .resultingStock(p.getStockQuantity() + 50)
                    .logDate(LocalDateTime.now().minusDays(30))
                    .build());

            if (p.getStockQuantity() > 0) {
                logs.add(InventoryLog.builder()
                        .product(p)
                        .changeType("ADJUSTMENT")
                        .quantityChange(-50)
                        .resultingStock(p.getStockQuantity())
                        .logDate(LocalDateTime.now().minusDays(2))
                        .build());
            }
        }
        inventoryLogRepository.saveAll(logs);

        // 3. Historical Sales: 30 days of data strictly ending yesterday (today.minusDays(1))
        // This ensures today's sales starts at 0 until real sales are transacted today
        ZoneId shopZone = ZoneId.of("Asia/Kolkata");
        LocalDate today = LocalDate.now(shopZone);
        List<Sale> sales = new ArrayList<>();

        int[] baseDailyQty = {4, 5, 8, 3, 4, 6, 4, 5, 3, 3, 4, 3};

        for (int dayOffset = 30; dayOffset >= 1; dayOffset--) {
            LocalDate saleDay = today.minusDays(dayOffset);
            LocalDateTime saleTime = saleDay.atTime(14, 0);

            for (int pIdx = 0; pIdx < products.size(); pIdx++) {
                Product p = products.get(pIdx);
                int variance = (dayOffset % 3 == 0) ? 1 : (dayOffset % 5 == 0 ? -1 : 0);
                int qty = Math.max(1, baseDailyQty[pIdx] + variance);
                BigDecimal total = p.getPrice().multiply(BigDecimal.valueOf(qty));

                sales.add(Sale.builder()
                        .product(p)
                        .quantitySold(qty)
                        .unitPrice(p.getPrice())
                        .totalAmount(total)
                        .saleDate(saleTime)
                        .user(demoUser)
                        .build());
            }
        }
        saleRepository.saveAll(sales);

        // 4. Initial Alert Checks
        for (Product p : products) {
            alertService.checkAndCreateAlerts(p);
        }

        // 5. Initial AI Forecast Calculations
        forecastService.generateForecastForAllProducts();

        log.info("Demo seeding completed successfully! Inserted {} products, {} sales records up to yesterday.",
                products.size(), sales.size());
    }

    private Product createProduct(String name, String sku, String category, double price, int stock, int minStock, String supplier) {
        return Product.builder()
                .name(name)
                .sku(sku)
                .category(category)
                .price(BigDecimal.valueOf(price))
                .stockQuantity(stock)
                .minStockLevel(minStock)
                .supplier(supplier)
                .build();
    }
}
