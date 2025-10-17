package com.donttouch.external_assistant_service.domain.chart.entity.vo;

import com.donttouch.common_service.stock.entity.Stock;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockSearchResponse {
    private String stockName;
    private String symbol;

    public static StockSearchResponse fromEntity(Stock stock) {
        return StockSearchResponse.builder()
                .stockName(stock.getStockName())
                .symbol(stock.getSymbol())
                .build();
    }
}