package com.donttouch.external_assistant_service.domain.chart.entity;

import com.donttouch.common_service.stock.entity.Stock;
import jakarta.persistence.*;

import java.time.LocalDateTime;

public class Trade {
    @Id
    @Column(name = "user_trade_id", nullable = false, unique = true)
    private String userTradeId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Column(name = "trade_ts", nullable = false)
    private LocalDateTime tradeTs;

    @Column(name = "quantity", nullable = false)
    private Double quantity;

    @Column(name = "price", nullable = false)
    private Double price;

    @Enumerated(EnumType.STRING)
    @Column(name = "side", nullable = false)
    private Side side;
}
