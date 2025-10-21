package com.donttouch.chart_similarity_service.domain.my_stock.entity.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockMainSignalResponse {
    private String symbol;
    private boolean buySignal;
    private boolean sellSignal;
}