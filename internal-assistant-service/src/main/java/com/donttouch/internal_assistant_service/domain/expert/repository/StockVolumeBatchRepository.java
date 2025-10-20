package com.donttouch.internal_assistant_service.domain.expert.repository;

import com.donttouch.common_service.auth.entity.InvestmentType;
import com.donttouch.internal_assistant_service.domain.expert.entity.batch.StockVolumeBatch;
import com.donttouch.internal_assistant_service.domain.member.entity.Side;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockVolumeBatchRepository extends JpaRepository<StockVolumeBatch, Long> {

    @Query("SELECT s FROM StockVolumeBatch s WHERE s.side = :side AND s.stockPeriod = :investmentType")
    List<StockVolumeBatch> findBySideAndInvest(@Param("side") Side side,
                                  @Param("investmentType") InvestmentType investmentType);

    void deleteBySideAndStockPeriod(Side side, InvestmentType type);
}
