package com.donttouch.chart_similarity_service.domain.my_stock.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stocks")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stocks {

    @Id
    @Column(name = "stock_id", columnDefinition = "CHAR(36)")
    private String stockId;   // ✅ UUID 문자열

    @Column(name = "sector_id", columnDefinition = "CHAR(36)")
    private String sectorId;  // nullable 허용

    @Column(name = "symbol", nullable = false)
    private String symbol;    // 종목코드 (e.g. 005930)

    @Column(name = "stock_name", nullable = false)
    private String stockName; // 종목명 (e.g. 삼성전자)

    @Enumerated(EnumType.STRING)
    @Column(name = "market", nullable = false)
    private MarketType market; // ✅ enum('KOSPI','KOSDAQ','KONEX')

    @Column(name = "management", columnDefinition = "TINYINT(1)", nullable = true)
    private Boolean management; // 관리종목 여부 (0/1)

    @Column(name = "delisting", columnDefinition = "TINYINT(1)", nullable = true)
    private Boolean delisting;  // 상장폐지 여부 (0/1)

    // ⚙️ 내부 enum 정의
    public enum MarketType {
        KOSPI, KOSDAQ, KONEX
    }
}
