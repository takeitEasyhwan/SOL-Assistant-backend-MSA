package com.donttouch.internal_assistant_service.domain.member.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HoldingPeriodResponse {
    private double averageHoldingDays; // 나의 평균 보유일
    private double quantile;           // 상위 몇 퍼센트
}