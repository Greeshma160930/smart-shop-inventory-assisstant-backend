package com.smartshop.repository;

import com.smartshop.entity.Product;
import com.smartshop.entity.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    List<Sale> findByProductOrderBySaleDateAsc(Product product);

    List<Sale> findByProductIdOrderBySaleDateAsc(Long productId);

    @Query("SELECT s FROM Sale s WHERE " +
           "(:productId IS NULL OR s.product.id = :productId) AND " +
           "(:fromDate IS NULL OR s.saleDate >= :fromDate) AND " +
           "(:toDate IS NULL OR s.saleDate < :toDate) " +
           "ORDER BY s.saleDate DESC")
    Page<Sale> findFilteredSales(@Param("productId") Long productId,
                                @Param("fromDate") LocalDateTime fromDate,
                                @Param("toDate") LocalDateTime toDate,
                                Pageable pageable);

    @Query("SELECT s FROM Sale s WHERE " +
           "(:fromDate IS NULL OR s.saleDate >= :fromDate) AND " +
           "(:toDate IS NULL OR s.saleDate < :toDate) " +
           "ORDER BY s.saleDate DESC")
    List<Sale> findSalesForExport(@Param("fromDate") LocalDateTime fromDate,
                                 @Param("toDate") LocalDateTime toDate);

    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s WHERE s.saleDate >= :startOfDay AND s.saleDate < :endOfDay")
    BigDecimal sumTotalSalesToday(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT COALESCE(SUM(s.quantitySold), 0) FROM Sale s WHERE s.saleDate >= :startOfDay AND s.saleDate < :endOfDay")
    long sumQuantitySoldToday(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s WHERE s.saleDate >= :start AND s.saleDate < :end")
    BigDecimal sumTotalSalesBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(s.quantitySold), 0) FROM Sale s WHERE s.saleDate >= :start AND s.saleDate < :end")
    long sumQuantitySoldBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s")
    BigDecimal sumTotalRevenue();

    @Query("SELECT COALESCE(SUM(s.quantitySold), 0) FROM Sale s")
    long sumTotalQuantitySold();

    List<Sale> findTop10ByOrderBySaleDateDesc();
}
