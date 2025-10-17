package com.donttouch.chart_similarity_service.domain.all_stock.controller;

import com.donttouch.chart_similarity_service.domain.all_stock.service.AllStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/insight/chart-similarity")
@RequiredArgsConstructor
public class AllStockController {

    private final AllStockService allStockService;

    /**
     * ✅ 전체 종목의 시그널 조회 API
     * - signal-type: buy or sell
     * 예: GET /api/v1/insight/chart-similarity/all-stock?signal-type=buy
     */
    @GetMapping("/all-stock")
    public ResponseEntity<List<Map<String, Object>>> getAllSignalStocks(
            @RequestParam("signal-type") String signalType
    ) {
        log.info("📩 [AllStockController] 전체 종목 시그널 요청: signal-type={}", signalType);
        List<Map<String, Object>> result = allStockService.getAllSignalStocks(signalType);
        return ResponseEntity.ok(result);
    }
}
