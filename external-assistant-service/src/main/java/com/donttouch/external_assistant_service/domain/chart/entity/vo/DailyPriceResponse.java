package com.donttouch.external_assistant_service.domain.chart.entity.vo;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DailyPriceResponse {
    private String symbol;
    private String stockName;
    private Double previousClose;
    private Double prePreviousClose;

    public static DailyPriceResponse of(String symbol, String stockName, Double previousClose) {
        return DailyPriceResponse.builder()
                .stockName(stockName)
                .symbol(symbol)
                .previousClose(previousClose)
                .build();
    }

    public static DailyPriceResponse ofWithPrePrevious(String symbol, String stockName, Double previousClose, Double prePreviousClose) {
        return DailyPriceResponse.builder()
                .symbol(symbol)
                .stockName(stockName)
                .previousClose(previousClose)
                .prePreviousClose(prePreviousClose)
                .build();
    }
}
