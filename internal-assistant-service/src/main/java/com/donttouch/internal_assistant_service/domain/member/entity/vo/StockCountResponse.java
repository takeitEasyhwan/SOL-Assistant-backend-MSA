package com.donttouch.internal_assistant_service.domain.member.entity.vo;

import com.donttouch.common_service.stock.entity.Market;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StockCountResponse {
    private Double quantity;
}
