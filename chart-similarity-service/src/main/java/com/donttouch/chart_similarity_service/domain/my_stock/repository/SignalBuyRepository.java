package com.donttouch.chart_similarity_service.domain.my_stock.repository;

import com.donttouch.chart_similarity_service.domain.my_stock.entity.SignalBuy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SignalBuyRepository extends JpaRepository<SignalBuy, Long> {

    List<SignalBuy> findByStockCodeIn(List<String> stockCodes);

    Optional<SignalBuy> findTopByStockCodeOrderByCreatedAtDesc(String stockCode);
}
