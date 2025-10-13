package com.donttouch.external_assistant_service.domain.chart.entity.vo;

import com.donttouch.common_service.stock.entity.Stock;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StockRiskResponse {
    private String stockId;
    private boolean management; // 관리종목, 투자주의, 정상 등

    public static StockRiskResponse fromEntity(Stock stock) {
        return StockRiskResponse.builder()
                .stockId(stock.getId())
                .management(stock.getManagement())
                .build();
    }
}
