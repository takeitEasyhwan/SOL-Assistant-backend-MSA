package com.donttouch.internal_assistant_service.domain.member.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TradeSummary {

    private final double totalBuyAmount;
    private final double totalSellAmount;
    private final int buyCount;
    private final int sellCount;
    private final double realizedProfit;
    private final double realizedPercent;

}