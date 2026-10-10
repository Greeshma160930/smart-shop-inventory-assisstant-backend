package com.smartshop.repository;

import com.smartshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithLock(@Param("id") Long id);

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, Long id);

    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= p.minStockLevel")
    List<Product> findLowStockProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity <= p.minStockLevel")
    long countLowStockProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity = 0")
    long countOutOfStockProducts();

    @Query("SELECT DISTINCT p.category FROM Product p ORDER BY p.category ASC")
    List<String> findDistinctCategories();

    @Query("SELECT p FROM Product p WHERE " +
           "(:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:category IS NULL OR :category = '' OR p.category = :category) AND " +
           "(:status IS NULL OR :status = '' OR " +
           " (:status = 'IN_STOCK' AND p.stockQuantity > p.minStockLevel) OR " +
           " (:status = 'LOW_STOCK' AND p.stockQuantity <= p.minStockLevel AND p.stockQuantity > 0) OR " +
           " (:status = 'OUT_OF_STOCK' AND p.stockQuantity = 0))")
    Page<Product> searchProducts(@Param("search") String search,
                                @Param("category") String category,
                                @Param("status") String status,
                                Pageable pageable);
}
