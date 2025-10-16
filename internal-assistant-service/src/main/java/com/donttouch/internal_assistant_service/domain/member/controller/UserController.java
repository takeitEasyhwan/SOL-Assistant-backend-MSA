package com.donttouch.internal_assistant_service.domain.member.controller;

import com.donttouch.common_service.global.aop.AssignCurrentMemberId;
import com.donttouch.common_service.global.aop.dto.CurrentMemberIdRequest;
import com.donttouch.internal_assistant_service.domain.member.entity.vo.MyStockResponse;
import com.donttouch.internal_assistant_service.domain.member.entity.vo.TradeMoneyResponse;
import com.donttouch.internal_assistant_service.domain.member.entity.vo.TradeProfitResponse;
import com.donttouch.internal_assistant_service.domain.member.entity.vo.TradeSectorResponse;
import com.donttouch.internal_assistant_service.domain.member.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/trade-record")
    public void tradeRecord() {

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

    @GetMapping("/trade-profit/trade-info")
    public void tradeProfitTradeInfo(@PathVariable LocalDateTime dateTime) {

    }
}
