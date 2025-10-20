package com.donttouch.internal_assistant_service.domain.member.controller;

import com.donttouch.common_service.global.aop.AssignCurrentMemberId;
import com.donttouch.common_service.global.aop.dto.CurrentMemberIdRequest;
import com.donttouch.internal_assistant_service.domain.member.entity.vo.*;
import com.donttouch.internal_assistant_service.domain.member.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internal/member/report")
public class UserController {

    private final UserService userService;

    @GetMapping("/my-stock")
    @AssignCurrentMemberId
    public ResponseEntity<List<MyStockResponse>> getMyStocks(CurrentMemberIdRequest currentUser) {
        List<MyStockResponse> stockResponses = userService.getMyStocks(currentUser.getUserUuid());
        return ResponseEntity.ok(stockResponses);
    }

    @GetMapping("/{symbol}/count")
    @AssignCurrentMemberId
    public ResponseEntity<StockCountResponse> getStockCount(@PathVariable String symbol, CurrentMemberIdRequest currentUser) {
        StockCountResponse stockResponses = userService.getStockCount(symbol,currentUser.getUserUuid());
        return ResponseEntity.ok(stockResponses);
    }

    @GetMapping("/trade-profit/trade-type")
    @AssignCurrentMemberId
    public ResponseEntity<TradeTypeResponse> tradeProfitTradeInfo(CurrentMemberIdRequest currentUser) {
        TradeTypeResponse tradeTypeResponse = userService.getTradeType(currentUser.getUserUuid());
        return ResponseEntity.ok(tradeTypeResponse);
    }

    @GetMapping("/trade-record")
    @AssignCurrentMemberId
    public ResponseEntity<HoldingPeriodResponse> getTradeRecord(CurrentMemberIdRequest currentUser) {
        return ResponseEntity.ok(userService.getTradeRecord(currentUser.getUserUuid()));
    }

    @GetMapping("/trade-sector")
    @AssignCurrentMemberId
    public ResponseEntity<TradeSectorResponse> getTradeSector(CurrentMemberIdRequest currentUser) {
        return ResponseEntity.ok(userService.getTradeSector(currentUser.getUserUuid()));
    }

    @GetMapping("/trade-profit/hasmonth")
    @AssignCurrentMemberId
    public ResponseEntity<TradeHasMonthResponse> getTradeMonth(CurrentMemberIdRequest currentUser) {
        return ResponseEntity.ok(userService.getTradeMonths(currentUser.getUserUuid()));
    }

    @GetMapping("/trade-profit/thisMonth")
    @AssignCurrentMemberId
    public ResponseEntity<TradeProfitResponse> getTradeProfit(CurrentMemberIdRequest currentUser) {
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        return ResponseEntity.ok(userService.getTradeSimpleProfit(currentUser.getUserUuid(), start));
    }

    @GetMapping("/trade-profit/{month}")
    @AssignCurrentMemberId
    public ResponseEntity<TradeProfitResponse> getTradeProfit(@PathVariable String month, CurrentMemberIdRequest currentUser) {
        LocalDate start = LocalDate.parse(month+"-01");
        return ResponseEntity.ok(userService.getTradeProfit(currentUser.getUserUuid(), start));
    }

    @GetMapping("/trade-profit/sep/{month}")
    @AssignCurrentMemberId
    public ResponseEntity <TradeProfitResponse> getTradeProfitSep(@PathVariable String month, CurrentMemberIdRequest currentUser) {
        LocalDate start = LocalDate.parse(month+"-01");
        return ResponseEntity.ok(userService.getTradeProfitSep(currentUser.getUserUuid(), start));
    }

    @GetMapping("/trade-money")
    @AssignCurrentMemberId
    public ResponseEntity<TradeMoneyResponse> tradeMoney(CurrentMemberIdRequest currentUser) {
        return ResponseEntity.ok(userService.getTradeMoney(currentUser.getUserUuid()));
    }

}
