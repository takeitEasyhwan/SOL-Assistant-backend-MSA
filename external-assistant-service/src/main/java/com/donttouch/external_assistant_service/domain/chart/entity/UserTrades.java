package com.donttouch.external_assistant_service.domain.chart.entity;

import com.donttouch.common_service.auth.entity.User;
import com.donttouch.common_service.stock.entity.Stock;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "user_trades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserTrades {
    @Id
    @Column(name = "user_trades_id", nullable = false, unique = true)
    private String userTradeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

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
