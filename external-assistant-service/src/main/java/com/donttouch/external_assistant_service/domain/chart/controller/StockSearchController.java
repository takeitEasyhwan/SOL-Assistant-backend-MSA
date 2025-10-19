package com.donttouch.external_assistant_service.domain.chart.controller;

import com.donttouch.external_assistant_service.domain.chart.entity.vo.StockSearchResponse;
import com.donttouch.external_assistant_service.domain.chart.service.StockSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/v1/external/chart")
@RequiredArgsConstructor
public class StockSearchController {

    private final StockSearchService stockSearchService;

    @GetMapping("/search/{stockName}")
    public ResponseEntity<List<StockSearchResponse>> searchStocks(@PathVariable String stockName) {
        List<StockSearchResponse> results = stockSearchService.searchStocks(stockName);
        return ResponseEntity.ok(results);
    }
}