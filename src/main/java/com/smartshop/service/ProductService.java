package com.smartshop.service;

import com.smartshop.dto.ProductDto;
import com.smartshop.entity.InventoryLog;
import com.smartshop.entity.Product;
import com.smartshop.exception.ResourceNotFoundException;
import com.smartshop.repository.InventoryLogRepository;
import com.smartshop.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final InventoryLogRepository inventoryLogRepository;
    private final AlertService alertService;

    public ProductService(ProductRepository productRepository, InventoryLogRepository inventoryLogRepository, AlertService alertService) {
        this.productRepository = productRepository;
        this.inventoryLogRepository = inventoryLogRepository;
        this.alertService = alertService;
    }

    public Page<ProductDto> getProducts(String search, String category, String status, Pageable pageable) {
        String cleanSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        String cleanCategory = (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("ALL")) ? category.trim() : null;
        String cleanStatus = (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("ALL")) ? status.trim() : null;

        return productRepository.searchProducts(cleanSearch, cleanCategory, cleanStatus, pageable)
                .map(this::mapToDto);
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ProductDto getProductById(Long id) {
        Product product = getProductEntityById(id);
        return mapToDto(product);
    }

    public Product getProductEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Transactional
    public ProductDto createProduct(ProductDto dto) {
        String sku = dto.getSku().trim().toUpperCase();
        if (productRepository.existsBySku(sku)) {
            throw new IllegalStateException("Product SKU already exists: " + sku);
        }

        Product product = Product.builder()
                .name(dto.getName().trim())
                .sku(sku)
                .category(dto.getCategory().trim())
                .price(dto.getPrice())
                .stockQuantity(dto.getStockQuantity())
                .minStockLevel(dto.getMinStockLevel())
                .supplier(dto.getSupplier() != null ? dto.getSupplier().trim() : null)
                .build();

        product = productRepository.save(product);

        if (product.getStockQuantity() > 0) {
            InventoryLog invLog = InventoryLog.builder()
                    .product(product)
                    .changeType("ADJUSTMENT")
                    .quantityChange(product.getStockQuantity())
                    .resultingStock(product.getStockQuantity())
                    .build();
            inventoryLogRepository.save(invLog);
        }

        alertService.checkAndCreateAlerts(product);

        return mapToDto(product);
    }

    @Transactional
    public ProductDto updateProduct(Long id, ProductDto dto) {
        Product product = getProductEntityById(id);

        String newSku = dto.getSku().trim().toUpperCase();
        if (productRepository.existsBySkuAndIdNot(newSku, id)) {
            throw new IllegalStateException("Another product with SKU already exists: " + newSku);
        }

        int previousStock = product.getStockQuantity();
        int newStock = dto.getStockQuantity();

        product.setName(dto.getName().trim());
        product.setSku(newSku);
        product.setCategory(dto.getCategory().trim());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(newStock);
        product.setMinStockLevel(dto.getMinStockLevel());
        product.setSupplier(dto.getSupplier() != null ? dto.getSupplier().trim() : null);

        product = productRepository.save(product);

        if (previousStock != newStock) {
            int change = newStock - previousStock;
            InventoryLog invLog = InventoryLog.builder()
                    .product(product)
                    .changeType("ADJUSTMENT")
                    .quantityChange(change)
                    .resultingStock(newStock)
                    .build();
            inventoryLogRepository.save(invLog);
        }

        alertService.checkAndCreateAlerts(product);

        return mapToDto(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductEntityById(id);
        productRepository.delete(product);
    }

    public List<ProductDto> getLowStockProducts() {
        return productRepository.findLowStockProducts().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<String> getCategories() {
        return productRepository.findDistinctCategories();
    }

    public ProductDto mapToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .category(product.getCategory())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .minStockLevel(product.getMinStockLevel())
                .supplier(product.getSupplier())
                .stockStatus(product.getStockStatus())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
