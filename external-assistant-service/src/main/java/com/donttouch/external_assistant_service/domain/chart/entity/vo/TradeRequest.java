package com.donttouch.external_assistant_service.domain.chart.entity.vo;

import com.donttouch.external_assistant_service.domain.chart.entity.Side;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TradeRequest {
    private String userTradeId;
    private String symbol;
    private String userId;
    private LocalDateTime tradeTs;
    private Double quantity;
    private Double price;
    private Side side;
}