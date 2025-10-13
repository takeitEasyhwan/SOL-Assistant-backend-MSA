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
    private String userTradeId;  // 거래 고유 ID (UUID)
    private String userId;       // 사용자 ID
    private String stockId;      // 종목 코드
    private LocalDateTime tradeTs; // 거래 시각
    private Double quantity;     // 수량
    private Double price;        // 거래 가격
    private Side side;           // 거래 방향 (BUY / SELL)


}