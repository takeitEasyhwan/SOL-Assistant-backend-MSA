package com.donttouch.internal_assistant_service.domain.member.entity.vo;
import com.donttouch.internal_assistant_service.domain.member.entity.Side;
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