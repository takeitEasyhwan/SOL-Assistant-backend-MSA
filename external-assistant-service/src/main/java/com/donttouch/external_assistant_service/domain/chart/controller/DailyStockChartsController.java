package com.donttouch.external_assistant_service.domain.chart.controller;

import com.donttouch.external_assistant_service.domain.chart.entity.DailyStockCharts;
import com.donttouch.external_assistant_service.domain.chart.service.DailyStockChartsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/external/chart")
public class DailyStockChartsController {
    private final DailyStockChartsService dailyStockChartsService;


    @GetMapping("/{symbol}/day")
    public ResponseEntity<List<DailyStockCharts>> getDailyCharts(@PathVariable String symbol) {
        List<DailyStockCharts> chartList = dailyStockChartsService.getDailyCharts(symbol);
        return ResponseEntity.ok(chartList);
    }

    @GetMapping("/{symbol}/price")
    public ResponseEntity<Map<String, Object>> getPreviousClosePrice(@PathVariable String symbol) {
        Double closePrice = dailyStockChartsService.getPreviousClosePrice(symbol);
        return ResponseEntity.ok(
                Map.of(
                        "symbol", symbol,
                        "previousClose", closePrice
                )
        );
    }
}
