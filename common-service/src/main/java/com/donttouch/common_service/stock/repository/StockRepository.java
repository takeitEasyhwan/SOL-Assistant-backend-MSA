package com.donttouch.common_service.stock.repository;

import com.donttouch.common_service.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, String> {
    Optional<Stock> findBySymbol(String symbol);

    @Query("""
        SELECT s FROM Stock s
        WHERE s.stockName LIKE CONCAT('%', :stockName, '%')
    """)
    List<Stock> findByStockNameContaining(@Param("stockName") String stockName);
}
