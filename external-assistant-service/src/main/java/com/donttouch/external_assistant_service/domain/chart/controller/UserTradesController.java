package com.donttouch.external_assistant_service.domain.chart.controller;

import com.donttouch.common_service.global.aop.AssignCurrentMemberId;
import com.donttouch.common_service.global.aop.dto.CurrentMemberIdRequest;
import com.donttouch.external_assistant_service.domain.chart.entity.Side;
import com.donttouch.external_assistant_service.domain.chart.entity.vo.TradeRequest;
import com.donttouch.external_assistant_service.domain.chart.entity.vo.TradeResponse;
import com.donttouch.external_assistant_service.domain.chart.service.UserTradesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/external/trade")
public class UserTradesController {
    private final UserTradesService userTradesService;

    @PostMapping("/buy")
    @AssignCurrentMemberId
    public ResponseEntity<TradeResponse> buyStock(CurrentMemberIdRequest currentUser, @RequestBody TradeRequest request) {
        request.setUserId(currentUser.getUserUuid());
        request.setSide(Side.BUY);

        TradeResponse response = userTradesService.buy(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sell")
    @AssignCurrentMemberId
    public ResponseEntity<TradeResponse> sellStock(CurrentMemberIdRequest currentUser, @RequestBody TradeRequest request) {
        request.setUserId(currentUser.getUserUuid());
        request.setSide(Side.SELL);

        TradeResponse response = userTradesService.sell(request);
        return ResponseEntity.ok(response);
    }
}
