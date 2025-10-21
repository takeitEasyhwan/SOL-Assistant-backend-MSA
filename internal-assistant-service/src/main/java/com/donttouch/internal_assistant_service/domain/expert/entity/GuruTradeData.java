package com.donttouch.internal_assistant_service.domain.expert.entity;


import java.time.LocalDate;

public class GuruTradeData {
    private LocalDate date;
    private Double buyVolume;
    private Double sellVolume;

    public GuruTradeData(LocalDate date, Double buyVolume, Double sellVolume) {
        this.date = date;
        this.buyVolume = buyVolume;
        this.sellVolume = sellVolume;
    }

    public LocalDate getDate() { return date; }
    public Double getBuyVolume() { return buyVolume; }
    public Double getSellVolume() { return sellVolume; }
}