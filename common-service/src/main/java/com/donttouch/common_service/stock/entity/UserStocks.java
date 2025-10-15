package com.donttouch.common_service.stock.entity;

import com.donttouch.common_service.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "user_stocks")
public class UserStocks {
    @Id
    @Column(name = "user_stocks_id", nullable = false, unique = true)
    private String userStockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Column(name = "quantity", nullable = false)
    private Double quantity;

    @Column(name = "cost_basis", nullable = false)
    private Double costBasis;
}
