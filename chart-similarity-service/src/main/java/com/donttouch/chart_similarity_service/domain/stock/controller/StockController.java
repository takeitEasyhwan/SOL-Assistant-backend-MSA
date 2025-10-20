package com.donttouch.chart_similarity_service.domain.stock.controller;

import com.donttouch.chart_similarity_service.domain.stock.dto.StockSignalRes;
import com.donttouch.chart_similarity_service.domain.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/v1/insight/chart-similarity")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping("/{stockCode}")
    public ResponseEntity<StockSignalRes> getStockSignal(
            @PathVariable String stockCode,
            @RequestParam("signal-type") String signalType
    ) {
        StockSignalRes response = stockService.getSignalInfo(stockCode, signalType);
        return ResponseEntity.ok(response);


    }
}
