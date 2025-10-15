package com.donttouch.chart_similarity_service.domain.my_stock.controller;

import com.donttouch.chart_similarity_service.domain.my_stock.entity.UserStock;
import com.donttouch.chart_similarity_service.domain.my_stock.service.MyStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/insight/chart-similarity")
@RequiredArgsConstructor
public class MyStockController {

    private final MyStockService myStockService;

    /**
     * 내 보유 종목 조회 API
     * 예: GET /api/v1/insight/chart-similarity/my-stock?userId=06cd3361-cb24-47a9-a5ef-62655ce1a397
     */
    @GetMapping("/my-stock")
    public ResponseEntity<List<UserStock>> getMyStocks(@RequestParam String userId) {
        log.info("📈 보유 종목 조회 요청: userId={}", userId);
        List<UserStock> stocks = myStockService.getMyStocks(userId);
        return ResponseEntity.ok(stocks);
    }
}
