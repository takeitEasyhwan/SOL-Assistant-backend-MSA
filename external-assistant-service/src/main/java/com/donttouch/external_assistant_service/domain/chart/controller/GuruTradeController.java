package com.donttouch.external_assistant_service.domain.chart.controller;

import com.donttouch.common_service.auth.entity.vo.InvestmentType;
import com.donttouch.external_assistant_service.domain.chart.entity.vo.GuruTradeResponse;
import com.donttouch.external_assistant_service.domain.chart.service.GuruTradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/external/chart")
public class GuruTradeController {
    private final GuruTradeService guruTradeService;

    @GetMapping("/{symbol}/guruTrade/{type}")
    public ResponseEntity<GuruTradeResponse> getGuruTrade(@PathVariable String symbol, @PathVariable String type) {
        InvestmentType investmentType = InvestmentType.valueOf(type.toUpperCase());
        GuruTradeResponse response = guruTradeService.getGuruTrade(symbol, investmentType);
        return ResponseEntity.ok(response);
    }
}
