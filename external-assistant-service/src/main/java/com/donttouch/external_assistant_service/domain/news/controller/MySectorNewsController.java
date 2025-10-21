package com.donttouch.external_assistant_service.domain.news.controller;

import com.donttouch.common_service.global.aop.AssignCurrentMemberId;
import com.donttouch.common_service.global.aop.dto.CurrentMemberIdRequest;
import com.donttouch.external_assistant_service.domain.news.entity.vo.SectorNewsInfoResponse;
import com.donttouch.external_assistant_service.domain.news.entity.vo.StockMainNewsResponse;
import com.donttouch.external_assistant_service.domain.news.service.MySectorNewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/external/news")
@RequiredArgsConstructor
@Slf4j
public class MySectorNewsController {

    private final MySectorNewsService mySectorNewsService;

    @GetMapping("/my-sector/news-info")
    @AssignCurrentMemberId
    public ResponseEntity<List<SectorNewsInfoResponse>> getMySectorNewsInfo(CurrentMemberIdRequest currentUser) {
        List<SectorNewsInfoResponse> data = mySectorNewsService.getMySectorNewsInfo(currentUser.getUserUuid());
        return ResponseEntity.ok(data);
    }

    @GetMapping("/stock-main/{symbol}")
    @AssignCurrentMemberId
    public ResponseEntity<StockMainNewsResponse> getStockMainNews(@PathVariable String symbol) {
        StockMainNewsResponse data = mySectorNewsService.getStockMainNews(symbol);
        return ResponseEntity.ok(data);
    }



}