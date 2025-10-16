package com.donttouch.internal_assistant_service.domain.member.entity.vo;
import com.donttouch.internal_assistant_service.domain.member.entity.Side;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TradeResponse {
    private String userTradeId;
    private String stockId;
    private String userId;
    private LocalDateTime tradeTs;
    private Double quantity;
    private Double price;
    private Side side;
    private String status;
}