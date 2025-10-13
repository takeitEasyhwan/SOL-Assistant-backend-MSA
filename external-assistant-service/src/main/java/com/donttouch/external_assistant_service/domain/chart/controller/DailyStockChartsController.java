package com.donttouch.external_assistant_service.domain.chart.controller;

import com.donttouch.external_assistant_service.domain.chart.entity.DailyStockCharts;
import com.donttouch.external_assistant_service.domain.chart.entity.vo.DailyPriceResponse;
import com.donttouch.external_assistant_service.domain.chart.entity.vo.DailyStockChartsResponse;
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
    public ResponseEntity<List<DailyStockChartsResponse>> getDailyCharts(@PathVariable String symbol) {
        List<DailyStockChartsResponse> chartList = dailyStockChartsService.getDailyCharts(symbol);
        return ResponseEntity.ok(chartList);
    }

    @GetMapping("/{symbol}/price")
    public ResponseEntity<DailyPriceResponse> getPreviousClosePrice(@PathVariable String symbol) {
        Double closePrice = dailyStockChartsService.getPreviousClosePrice(symbol);
        return ResponseEntity.ok(DailyPriceResponse.of(symbol,closePrice));
    }
}
