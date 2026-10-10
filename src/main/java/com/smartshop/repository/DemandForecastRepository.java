package com.smartshop.repository;

import com.smartshop.entity.DemandForecast;
import com.smartshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DemandForecastRepository extends JpaRepository<DemandForecast, Long> {

    Optional<DemandForecast> findFirstByProductIdAndForecastDateBetween(Long productId, LocalDateTime start, LocalDateTime end);

    Optional<DemandForecast> findTopByProductOrderByForecastDateDesc(Product product);

    Optional<DemandForecast> findTopByProductIdOrderByForecastDateDesc(Long productId);

    List<DemandForecast> findByProductIdOrderByForecastDateDesc(Long productId);

    @Query(value = "SELECT df.* FROM demand_forecasts df " +
                   "INNER JOIN (SELECT product_id, MAX(forecast_date) as max_date FROM demand_forecasts GROUP BY product_id) latest " +
                   "ON df.product_id = latest.product_id AND df.forecast_date = latest.max_date",
           nativeQuery = true)
    List<DemandForecast> findLatestForecasts();

    @Query(value = "SELECT COALESCE(SUM(df.predicted_demand), 0) FROM demand_forecasts df " +
                   "INNER JOIN (SELECT product_id, MAX(forecast_date) as max_date FROM demand_forecasts GROUP BY product_id) latest " +
                   "ON df.product_id = latest.product_id AND df.forecast_date = latest.max_date",
           nativeQuery = true)
    Double sumTotalPredictedDemandNext7Days();
}
