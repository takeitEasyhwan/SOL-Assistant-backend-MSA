package com.donttouch.chart_similarity_service.domain.stock.dto;//package com.donttouch.chart_similarity_service.domain.stock.dto;
//
//import lombok.*;
//
//import java.util.List;
//import java.util.Map;
//
//@Getter
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//public class StockSignalRes {
//    private String stockName;
//    private List<Map<String, Object>> trendPastScaled;
//    private List<Map<String, Object>> trendToday;
//    private String todayDate;
//    private String pastDate;
//    private String description;
//    private String descriptionDetail;
//}


import lombok.*;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockSignalRes {
    private String stockName;
    private String signalType;
    private List<Map<String, Object>> trendToday;
    private List<Map<String, Object>> trendPastScaled;
    private String todayDate;
    private String pastDate;
    private String description;
    private String descriptionDetail;
}
