package com.donttouch.internal_assistant_service.domain.expert.entity.vo;

import com.donttouch.common_service.auth.entity.InvestmentType;
import com.donttouch.common_service.stock.entity.Stock;
import com.donttouch.internal_assistant_service.domain.expert.entity.GuruTradeData;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuruTradeResponse {
    private String symbol;
    private String stockName;
    private InvestmentType period;
    private List<GuruTradeData> data;

    public static GuruTradeResponse of(Stock stock, InvestmentType period,
                                       List<GuruTradeData> tradeStats,
                                       Double totalHolding) {
        tradeStats.forEach(d -> d.setHoldingVolume(totalHolding));
        return GuruTradeResponse.builder()
                .symbol(stock.getSymbol())
                .stockName(stock.getStockName())
                .period(period)
                .data(tradeStats)
                .build();
    }
}

