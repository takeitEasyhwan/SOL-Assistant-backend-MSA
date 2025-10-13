package com.donttouch.common_service.stock.repository;

import com.donttouch.common_service.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, String> {
    Optional<Stock> findBySymbol(String symbol);
}
