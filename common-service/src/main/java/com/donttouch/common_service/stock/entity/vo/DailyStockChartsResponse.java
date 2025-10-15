package com.donttouch.common_service.stock.entity.vo;

import com.donttouch.common_service.stock.entity.DailyStockCharts;
import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DailyStockChartsResponse {
    private String symbol;
    private LocalDate date;
    private Double openPrice;
    private Double closePrice;
    private Double highPrice;
    private Double lowPrice;
    private Long volume;
    public static DailyStockChartsResponse fromEntity(DailyStockCharts chart) {
        return DailyStockChartsResponse.builder()
                .symbol(chart.getStock().getSymbol())
                .date(chart.getCurrentDay())
                .openPrice(chart.getOpenPrice())
                .closePrice(chart.getClosePrice())
                .highPrice(chart.getHighPrice())
                .lowPrice(chart.getLowPrice())
                .volume(chart.getVolume())
                .build();
    }
}
