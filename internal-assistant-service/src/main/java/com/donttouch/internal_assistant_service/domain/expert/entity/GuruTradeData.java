package com.donttouch.internal_assistant_service.domain.expert.entity;


import java.time.LocalDate;

public class GuruTradeData {
    private LocalDate date;
    private Double buyVolume;
    private Double sellVolume;
    private Double holdingVolume;

    public GuruTradeData(LocalDate date, Double buyVolume, Double sellVolume, Double holdingVolume) {
        this.date = date;
        this.buyVolume = buyVolume;
        this.sellVolume = sellVolume;
        this.holdingVolume = holdingVolume;
    }

    public LocalDate getDate() { return date; }
    public Double getBuyVolume() { return buyVolume; }
    public Double getSellVolume() { return sellVolume; }
    public Double getHoldingVolume() { return holdingVolume; }
}