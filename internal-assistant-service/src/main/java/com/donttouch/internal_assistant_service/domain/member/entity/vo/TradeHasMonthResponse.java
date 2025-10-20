package com.donttouch.internal_assistant_service.domain.member.entity.vo;

import com.donttouch.internal_assistant_service.domain.member.entity.Side;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class TradeHasMonthResponse {
    private List<String> months;
    private int totalMonths;
}
