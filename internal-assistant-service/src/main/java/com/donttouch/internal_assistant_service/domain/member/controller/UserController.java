package com.donttouch.internal_assistant_service.domain.member.controller;

import com.donttouch.common_service.global.aop.AssignCurrentMemberId;
import com.donttouch.common_service.global.aop.dto.CurrentMemberIdRequest;
import com.donttouch.internal_assistant_service.domain.member.entity.vo.*;
import com.donttouch.internal_assistant_service.domain.member.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internal/member/report")
public class UserController {

    private final UserService userService;

    @GetMapping("/my-stock")
    @AssignCurrentMemberId
    public List<MyStockResponse> getMyStocks(CurrentMemberIdRequest currentUser) {
        List<MyStockResponse> stockResponses = userService.getMyStocks(currentUser.getUserUuid());
        return stockResponses;
    }

    @GetMapping("/trade-profit/trade-type")
    @AssignCurrentMemberId
    public TradeTypeResponse tradeProfitTradeInfo(CurrentMemberIdRequest currentUser) {
        TradeTypeResponse tradeTypeResponse = userService.getTradeType(currentUser.getUserUuid());
        return tradeTypeResponse;
    }

    @GetMapping("/trade-record")
    @AssignCurrentMemberId
    public HoldingPeriodResponse getTradeRecord(CurrentMemberIdRequest currentUser) {
        return userService.getTradeRecord(currentUser.getUserUuid());
    }

    @GetMapping("/trade-sector")
    @AssignCurrentMemberId
    public TradeSectorResponse getTradeSector(CurrentMemberIdRequest currentUser) {
        return userService.getTradeSector(currentUser.getUserUuid());
    }

    @GetMapping("/trade-profit/thisMonth")
    @AssignCurrentMemberId
    public TradeProfitResponse getTradeProfit(CurrentMemberIdRequest currentUser) {
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        return userService.getTradeSimpleProfit(currentUser.getUserUuid(), start);
    }

    @GetMapping("/trade-profit/{month}")
    @AssignCurrentMemberId
    public TradeProfitResponse getTradeProfit(@PathVariable String month, CurrentMemberIdRequest currentUser) {
        LocalDate start = LocalDate.parse(month+"-01");
        return userService.getTradeProfit(currentUser.getUserUuid(), start);
    }

    @GetMapping("/trade-profit/sep/{month}")
    @AssignCurrentMemberId
    public TradeProfitResponse getTradeProfitSep(@PathVariable String month, CurrentMemberIdRequest currentUser) {
        LocalDate start = LocalDate.parse(month+"-01");
        return userService.getTradeProfitSep(currentUser.getUserUuid(), start);
    }

    @GetMapping("/trade-money")
    @AssignCurrentMemberId
    public TradeMoneyResponse tradeMoney(CurrentMemberIdRequest currentUser) {
        return userService.getTradeMoney(currentUser.getUserUuid());
    }

}
