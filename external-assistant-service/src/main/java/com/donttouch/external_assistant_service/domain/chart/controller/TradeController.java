package com.donttouch.external_assistant_service.domain.chart.controller;

import com.donttouch.external_assistant_service.domain.chart.entity.Side;
import com.donttouch.external_assistant_service.domain.chart.entity.vo.TradeRequest;
import com.donttouch.external_assistant_service.domain.chart.entity.vo.TradeResponse;
import com.donttouch.external_assistant_service.domain.chart.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/external/trade")
public class TradeController {
    private final TradeService tradeService;

    @PostMapping("/buy")
    public ResponseEntity<TradeResponse> buyStock(@RequestBody TradeRequest request) {
        request.setSide(Side.BUY);
        TradeResponse response = tradeService.buy(request);
        return ResponseEntity.ok(response);
    }
}
