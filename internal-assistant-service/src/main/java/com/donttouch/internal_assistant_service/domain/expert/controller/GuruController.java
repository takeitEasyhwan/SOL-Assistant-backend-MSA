package com.donttouch.internal_assistant_service.domain.expert.controller;


import com.donttouch.common_service.auth.entity.InvestmentType;
import com.donttouch.common_service.global.aop.AssignCurrentMemberId;
import com.donttouch.common_service.global.aop.dto.CurrentMemberIdRequest;
import com.donttouch.internal_assistant_service.domain.expert.entity.vo.*;
import com.donttouch.internal_assistant_service.domain.expert.service.GuruService;
import com.donttouch.internal_assistant_service.domain.member.entity.Side;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internal/expert")
public class GuruController {
    private final GuruService guruService;

    @GetMapping("/volume/{side}/{investmentType}")
    public ResponseEntity<GuruVolumeResponse> getGuruVolumeRank(@PathVariable("side") Side side, @PathVariable("investmentType") InvestmentType investmentType) {
        GuruVolumeResponse getGuruVolumeRank = guruService.getGuruVolumeRank(side, investmentType);
        return new ResponseEntity<>(getGuruVolumeRank, HttpStatus.OK);
    }

    @GetMapping("/view/{investmentType}")
    public ResponseEntity<GuruVolumeResponse> guruView(@PathVariable("investmentType") InvestmentType investmentType) {
        GuruVolumeResponse guruViewResponse = guruService.guruView(investmentType);
        return new ResponseEntity<>(guruViewResponse, HttpStatus.OK);
    }

    @GetMapping("/my-view")
    @AssignCurrentMemberId
    public ResponseEntity<GuruVolumeResponse> guruMyView(CurrentMemberIdRequest currentMemberIdRequest) {
        GuruVolumeResponse guruMViewRaynkResponse = guruService.getMyViewStocksGuru(currentMemberIdRequest);
        return new ResponseEntity<>(guruMViewRaynkResponse, HttpStatus.OK);
    }

    @GetMapping("/{symbol}/{investmentType}")
    public ResponseEntity<GuruTradeResponse> getGuruTrade(@PathVariable("symbol") String symbol, @PathVariable("investmentType") InvestmentType investmentType) {
        GuruTradeResponse response = guruService.getGuruTrade(symbol, investmentType);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/tracking")
    public ResponseEntity<UserTrackingResponse> tracking(@RequestBody UserTrackingRequest userTrackingRequest) {
        UserTrackingResponse userTrackingBatchResponse = guruService.collectBatch(userTrackingRequest.getEvents());
        return new ResponseEntity<>(userTrackingBatchResponse, HttpStatus.CREATED);
    }

    @GetMapping("/stock-main/{symbol}")
    @AssignCurrentMemberId
    public ResponseEntity<UserGuruMainResponse> getStockSymbolGuru(@PathVariable("symbol") String symbol, CurrentMemberIdRequest currentMemberIdRequest) {
        UserGuruMainResponse userGuruMainResponse = guruService.getStockSymbolGuru(symbol, currentMemberIdRequest);
        return new ResponseEntity<>(userGuruMainResponse, HttpStatus.OK);
    }
}
