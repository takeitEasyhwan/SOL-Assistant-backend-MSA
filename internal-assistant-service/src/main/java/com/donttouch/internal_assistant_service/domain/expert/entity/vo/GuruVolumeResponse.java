package com.donttouch.internal_assistant_service.domain.expert.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuruVolumeResponse {

    private String date;
    private List<GuruStockVolumeDto> stockVolumeList;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GuruStockVolumeDto {
        private String stockSymbol;
        private String stockName;
        private double yesterdayClosePrice;
        private double todayClosePrice;
        private double priceChangePercent;
        private double yesterdayVolume;
        private double todayVolume;
        private double volumeChangePercent;
    }
}
