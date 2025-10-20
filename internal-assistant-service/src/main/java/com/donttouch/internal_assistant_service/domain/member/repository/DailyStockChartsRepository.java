package com.donttouch.internal_assistant_service.domain.member.repository;

import com.donttouch.common_service.stock.entity.DailyStockCharts;
import com.donttouch.common_service.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyStockChartsRepository extends JpaRepository<DailyStockCharts, Long> {
    List<DailyStockCharts> findByStockOrderByCurrentDayDesc(Stock stock);

    Optional<DailyStockCharts> findTopByStockOrderByCurrentDayDesc(Stock stock);



    @Query("SELECT d FROM DailyStockCharts d WHERE d.stock.id IN :stockIds ORDER BY d.stock.id, d.currentDay DESC")
    List<DailyStockCharts> findByStockIds(@Param("stockIds") List<String> stockIds);

}