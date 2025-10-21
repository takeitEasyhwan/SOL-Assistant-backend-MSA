package com.donttouch.chart_similarity_service.domain.my_stock.repository;

import com.donttouch.chart_similarity_service.domain.my_stock.entity.SignalBuy;
import com.donttouch.chart_similarity_service.domain.my_stock.entity.SignalSell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SignalSellRepository extends JpaRepository<SignalSell, Long> {
    Optional<SignalSell> findByStockCode(String stockCode);

    List<SignalSell> findByStockCodeIn(List<String> stockCodes);

    Optional<SignalSell> findTopByStockCodeOrderByCreatedAtDesc(String stockCode);
}
