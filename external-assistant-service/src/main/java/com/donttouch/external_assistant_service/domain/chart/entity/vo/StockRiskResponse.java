package com.donttouch.external_assistant_service.domain.chart.entity.vo;

import com.donttouch.common_service.stock.entity.Stock;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StockRiskResponse {
    private String symbol;
    private String stockName;
    private boolean management;
    private boolean delisting;

    public static StockRiskResponse fromEntity(Stock stock) {
        return StockRiskResponse.builder()
                .symbol(stock.getSymbol())
                .stockName(stock.getStockName())
                .management(stock.getManagement())
                .delisting(stock.getDelisting())
                .build();
    }
}
