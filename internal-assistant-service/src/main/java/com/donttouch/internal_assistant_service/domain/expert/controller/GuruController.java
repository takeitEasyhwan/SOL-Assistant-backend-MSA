package com.donttouch.internal_assistant_service.domain.expert.controller;


import com.donttouch.common_service.auth.entity.InvestmentType;
import com.donttouch.common_service.global.aop.AssignCurrentMemberId;
import com.donttouch.internal_assistant_service.domain.expert.entity.vo.GuruTradeResponse;
import com.donttouch.internal_assistant_service.domain.expert.entity.vo.GuruVolumeResponse;
import com.donttouch.internal_assistant_service.domain.expert.entity.vo.UserTrackingRequest;
import com.donttouch.internal_assistant_service.domain.expert.entity.vo.UserTrackingResponse;
import com.donttouch.internal_assistant_service.domain.expert.service.GuruService;
import com.donttouch.internal_assistant_service.domain.member.entity.Side;
import com.donttouch.internal_assistant_service.domain.member.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internal/expertt")
public class GuruController {
    private final GuruService guruService;

//    @GetMapping("/volume/{type}/{period}")
//    public ResponseEntity<List<GuruVolumeResponse>> getGuruVolumeRank(@PathVariable String trade, @PathVariable String type) {
//        Side side = Side.valueOf(trade.toUpperCase());
//        InvestmentType investmentType = InvestmentType.valueOf(type.toUpperCase());
//        List<GuruVolumeResponse> response = guruService.getTopVolumeStocks(side, investmentType);
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/view")
    public void guruView() {

    }

    @GetMapping("/{symbol}/{type}")
    public ResponseEntity<GuruTradeResponse> getGuruTrade(@PathVariable String symbol, @PathVariable String type) {
        InvestmentType investmentType = InvestmentType.valueOf(type.toUpperCase());
        GuruTradeResponse response = guruService.getGuruTrade(symbol, investmentType);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/tracking")
    @AssignCurrentMemberId
    public ResponseEntity<UserTrackingResponse> tracking(@RequestBody UserTrackingRequest userTrackingRequest) {
        UserTrackingResponse userTrackingBatchResponse = guruService.collectBatch(userTrackingRequest.getEvents());
        return new ResponseEntity<>(userTrackingBatchResponse, HttpStatus.CREATED);
    }
}
