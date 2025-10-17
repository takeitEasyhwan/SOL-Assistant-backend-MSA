package com.donttouch.internal_assistant_service.domain.member.entity.vo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeMoneyResponse {
    private Double totalBalance;
    private Double principal;
}