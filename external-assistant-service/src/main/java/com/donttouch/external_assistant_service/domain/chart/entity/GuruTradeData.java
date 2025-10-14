package com.donttouch.external_assistant_service.domain.chart.entity;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GuruTradeData {
    private LocalDateTime date;
    private Double buyVolume;
    private Double sellVolume;
    private Double holdingVolume;

    public GuruTradeData(LocalDateTime date, Double buyVolume, Double sellVolume) {
        this.date = date;
        this.buyVolume = buyVolume;
        this.sellVolume = sellVolume;
    }
}
