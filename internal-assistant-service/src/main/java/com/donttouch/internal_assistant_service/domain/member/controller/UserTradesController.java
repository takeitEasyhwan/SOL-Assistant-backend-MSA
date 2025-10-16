package com.donttouch.internal_assistant_service.domain.member.controller;


import com.donttouch.common_service.global.aop.AssignCurrentMemberId;
import com.donttouch.common_service.global.aop.dto.CurrentMemberIdRequest;
import com.donttouch.internal_assistant_service.domain.member.entity.Side;
import com.donttouch.internal_assistant_service.domain.member.entity.vo.TradeRequest;
import com.donttouch.internal_assistant_service.domain.member.entity.vo.TradeResponse;
import com.donttouch.internal_assistant_service.domain.member.service.UserTradesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internal/member/trade")
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
