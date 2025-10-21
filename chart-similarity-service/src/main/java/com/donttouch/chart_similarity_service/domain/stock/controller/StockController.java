package com.donttouch.chart_similarity_service.domain.stock.controller;

import com.donttouch.chart_similarity_service.domain.stock.dto.StockSignalRes;
import com.donttouch.chart_similarity_service.domain.stock.service.StockService;
import com.donttouch.common_service.global.aop.AssignCurrentMemberId;
import com.donttouch.common_service.global.aop.dto.CurrentMemberIdRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/insight/chart-similarity")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping("/{stockCode}")
    @AssignCurrentMemberId
    public ResponseEntity<StockSignalRes> getStockSignal(
            @PathVariable String stockCode,
            @RequestParam("signal-type") String signalType,
            CurrentMemberIdRequest currentUser
    ) {
        StockSignalRes response = stockService.getSignalInfo(stockCode, signalType, currentUser.getUserUuid());
        return ResponseEntity.ok(response);
    }
}
