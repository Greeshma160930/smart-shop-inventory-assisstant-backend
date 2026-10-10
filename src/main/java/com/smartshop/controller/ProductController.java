package com.smartshop.controller;

import com.smartshop.dto.ProductDto;
import com.smartshop.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Endpoints for managing inventory products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "List, search, filter, and paginate products")
    public ResponseEntity<Page<ProductDto>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort) {

        String sortField = sort[0];
        Sort.Direction direction = (sort.length > 1 && sort[1].equalsIgnoreCase("asc")) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        return ResponseEntity.ok(productService.getProducts(search, category, status, pageable));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all products without pagination (for dropdowns)")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PostMapping
    @Operation(summary = "Create a new product")
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto dto) {
        ProductDto created = productService.createProduct(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing product")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDto dto) {
        return ResponseEntity.ok(productService.updateProduct(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product by ID")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(Map.of("message", "Product successfully deleted"));
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get list of low stock and out of stock products")
    public ResponseEntity<List<ProductDto>> getLowStockProducts() {
        return ResponseEntity.ok(productService.getLowStockProducts());
    }

    @GetMapping("/categories")
    @Operation(summary = "Get list of unique product categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(productService.getCategories());
    }
}
