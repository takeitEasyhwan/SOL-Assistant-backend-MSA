package com.donttouch.internal_assistant_service.domain.expert.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuruVolumeResponse {
    private Long stockId;
    private String symbol;
    private String stockName;

    private Double prevClose;
    private Double prevPrevClose;
    private Double closeChangeRate;

    private Double prevVolume;
    private Double prevPrevVolume;
    private Double volumeChangeRate;
}
