package com.donttouch.internal_assistant_service.domain.member.entity.vo;

import com.donttouch.common_service.auth.entity.InvestmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeTypeResponse {
    private String username;
    private InvestmentType investmentType;
}
