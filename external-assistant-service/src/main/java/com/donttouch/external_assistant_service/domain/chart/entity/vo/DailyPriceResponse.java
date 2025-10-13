package com.donttouch.external_assistant_service.domain.chart.entity.vo;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DailyPriceResponse {
    private String symbol;
    private Double previousClose;

    public static DailyPriceResponse of(String symbol, Double previousClose) {
        return DailyPriceResponse.builder()
                .symbol(symbol)
                .previousClose(previousClose)
                .build();
    }
}
