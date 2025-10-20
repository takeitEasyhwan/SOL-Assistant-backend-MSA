package com.donttouch.internal_assistant_service.domain.member.entity.vo;

import com.donttouch.common_service.stock.entity.Market;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MyStockResponse {
    private String stockName;
    private String symbol;
    private Double currentPrice;
    private Double changeRate;
    private Market market;
    private Double quantity;
    private Double costBasis;
    private Double diff;
    private Double profit;
    private LocalDateTime currentTradeTs;
}
