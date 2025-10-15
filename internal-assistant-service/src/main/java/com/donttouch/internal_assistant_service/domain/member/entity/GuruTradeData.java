package com.donttouch.internal_assistant_service.domain.member.entity;

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

    public GuruTradeData(LocalDateTime date, Double buyVolume, Double sellVolume) {
        this.date = date;
        this.buyVolume = buyVolume;
        this.sellVolume = sellVolume;
    }
}
