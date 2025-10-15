package com.donttouch.chart_similarity_service.domain.stock.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockSignalRes {

    private String stockName;
    private String trendPastScaled;   // ✅ 과거 스케일링된 데이터
    private String trendToday;        // ✅ 오늘 데이터 (선택)
    private String todayDate;
    private String pastDate;          // ✅ 과거 날짜
    private String description;
    private String descriptionDetail;
}
