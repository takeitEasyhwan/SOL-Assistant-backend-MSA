package com.donttouch.internal_assistant_service.domain.member.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController("/api/v1/internal/member")
public class UserController {

    @GetMapping("/my-stock/{UUID}")
    public void myStock(@PathVariable String UUID) {

    }

    @GetMapping("/trade-record")
    public void tradeRecord() {

    }

    @GetMapping("/trade-sector")
    public void tradeSector() {

    }

    @GetMapping("/trade-profit")
    public void tradeProfit() {

    }

    @GetMapping("/trade-money")
    public void tradeMoney() {

    }

    @GetMapping("/trade-profit/trade-info")
    public void tradeProfitTradeInfo(@PathVariable LocalDateTime dateTime) {

    }
}
