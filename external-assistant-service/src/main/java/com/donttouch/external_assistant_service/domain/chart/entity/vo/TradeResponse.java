package com.donttouch.external_assistant_service.domain.chart.entity.vo;

import com.donttouch.external_assistant_service.domain.chart.entity.Side;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TradeResponse {
    private String userTradeId;
    private String userId;
    private String stockId;
    private LocalDateTime tradeTs;
    private Double quantity;
    private Double price;
    private Side side;
    private String status;
}