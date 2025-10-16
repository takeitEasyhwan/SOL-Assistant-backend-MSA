package com.donttouch.internal_assistant_service.domain.member.entity.vo;

import com.donttouch.common_service.auth.entity.User;
import com.donttouch.common_service.auth.entity.vo.InvestmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeTypeResponse {
    private InvestmentType investmentType;
}
