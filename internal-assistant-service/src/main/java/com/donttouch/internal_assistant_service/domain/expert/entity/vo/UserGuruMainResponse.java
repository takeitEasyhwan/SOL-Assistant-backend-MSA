package com.donttouch.internal_assistant_service.domain.expert.entity.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class UserGuruMainResponse {
    private double guruSellPercent;
    private double guruBuyPercent;
    private double latestSellQuantity;
    private double latestBuyQuantity;
    private double prevSellQuantity;
    private double prevBuyQuantity;
    private boolean isDailyGuru;
}
