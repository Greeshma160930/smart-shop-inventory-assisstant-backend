package com.smartshop.repository;

import com.smartshop.entity.Alert;
import com.smartshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByIsReadFalseOrderByCreatedAtDesc();

    List<Alert> findAllByOrderByCreatedAtDesc();

    Page<Alert> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Alert> findByIsReadFalseOrderByCreatedAtDesc(Pageable pageable);

    long countByIsReadFalse();

    Optional<Alert> findFirstByProductAndTypeAndIsReadFalse(Product product, String type);

    List<Alert> findByProductAndTypeAndIsReadFalse(Product product, String type);

    List<Alert> findByProductAndIsReadFalse(Product product);

    void deleteByProduct(Product product);
}
