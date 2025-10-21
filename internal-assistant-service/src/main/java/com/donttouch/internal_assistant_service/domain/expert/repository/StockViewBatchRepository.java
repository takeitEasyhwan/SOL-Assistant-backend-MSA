package com.donttouch.internal_assistant_service.domain.expert.repository;

import com.donttouch.common_service.auth.entity.InvestmentType;
import com.donttouch.internal_assistant_service.domain.expert.entity.batch.StockViewBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockViewBatchRepository extends JpaRepository<StockViewBatch, Long> {

    @Query("SELECT s FROM StockViewBatch s WHERE s.stockPeriod = :investmentType")
    List<StockViewBatch> findByInvestment(@Param("investmentType") InvestmentType investmentType);

    void deleteByStockPeriod(InvestmentType type);

    boolean existsByStockIdAndStockPeriod(String stockId, InvestmentType stockPeriod);
}
