package com.donttouch.chart_similarity_service.domain.my_stock.repository;

import com.donttouch.chart_similarity_service.domain.my_stock.entity.Stocks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StocksRepository extends JpaRepository<Stocks, String> {
    List<Stocks> findAllBySymbolIn(List<String> symbols);

}
