package com.donttouch.external_assistant_service.domain.chart.service;


import com.donttouch.common_service.stock.repository.StockRepository;
import com.donttouch.external_assistant_service.domain.chart.entity.vo.StockSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockSearchService {
    private final StockRepository stockRepository;

    public List<StockSearchResponse> searchStocks(String stockName) {
        return stockRepository.findByStockNameContaining(stockName).stream()
                .map(StockSearchResponse::fromEntity)
                .toList();
    }
}
