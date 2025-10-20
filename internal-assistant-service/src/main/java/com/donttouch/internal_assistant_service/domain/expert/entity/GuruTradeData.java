package com.donttouch.internal_assistant_service.domain.expert.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public GuruTradeData(LocalDateTime date, Number buyVolume, Number sellVolume) {
        this.date = date;
        this.buyVolume = buyVolume != null ? buyVolume.doubleValue() : 0.0;
        this.sellVolume = sellVolume != null ? sellVolume.doubleValue() : 0.0;
        this.holdingVolume = 0.0;
    }

    public GuruTradeData(LocalDateTime date, Double buyVolume, Double sellVolume) {
        this.date = date;
        this.buyVolume = buyVolume != null ? buyVolume : 0.0;
        this.sellVolume = sellVolume != null ? sellVolume : 0.0;
        this.holdingVolume = 0.0;
    }
}
