package com.donttouch.internal_assistant_service.domain.member.entity.vo;

import com.donttouch.internal_assistant_service.domain.member.entity.Side;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class TradeProfitResponse {
    private double realizedProfit;
    private double realizedPercent;
    private long sellCount;
    private double sellAmount;
    private long buyCount;
    private double buyAmount;
    private List<TradeDetail> sellList;
    private List<TradeDetail> buyList;
    private List<TradeDetail> tradeList;

    @Data
    @Builder
    public static class TradeDetail {
        private String stockId;
        private String stockName;
        private String symbol;
        private double price;
        private double quantity;
        private double totalPrice;
        private Side side;
        private LocalDateTime tradeDate;
    }
}
