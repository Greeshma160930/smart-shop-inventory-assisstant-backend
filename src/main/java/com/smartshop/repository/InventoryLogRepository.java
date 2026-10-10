package com.smartshop.repository;

import com.smartshop.entity.InventoryLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {

    List<InventoryLog> findByProductIdOrderByLogDateDesc(Long productId);

    Page<InventoryLog> findAllByOrderByLogDateDesc(Pageable pageable);

    @Query("SELECT l FROM InventoryLog l WHERE (:productId IS NULL OR l.product.id = :productId) ORDER BY l.logDate DESC")
    Page<InventoryLog> findFilteredLogs(@Param("productId") Long productId, Pageable pageable);
}
