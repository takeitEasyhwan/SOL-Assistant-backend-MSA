package com.donttouch.internal_assistant_service.domain.expert.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GuruVolumeRankDto {
    private Long stockId;
    private String symbol;
    private String stockName;
    private Double prevVolume;
    private Double prevPrevVolume;

    public double getVolumeChangeRate() {
        if (prevPrevVolume == null || prevPrevVolume == 0) return 0;
        return (prevVolume - prevPrevVolume) / prevPrevVolume * 100;
    }
}