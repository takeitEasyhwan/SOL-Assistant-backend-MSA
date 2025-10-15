package com.donttouch.internal_assistant_service.domain.member.repository;

import com.donttouch.common_service.stock.entity.DailyStockCharts;
import com.donttouch.common_service.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DailyStockChartsRepository extends JpaRepository<DailyStockCharts, Long> {
    List<DailyStockCharts> findByStockOrderByCurrentDayDesc(Stock stock);
}