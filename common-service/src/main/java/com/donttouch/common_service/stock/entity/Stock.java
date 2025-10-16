package com.donttouch.common_service.stock.entity;

import com.donttouch.common_service.sector.entity.Sector;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stocks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Stock {

    @Id
    @Column(name = "stock_id", nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id", nullable = false)
    private Sector sector;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "stock_name")
    private String stockName;

    @Enumerated(EnumType.STRING)
    @Column(name = "market")
    private Market market;

    @Column(name = "management")
    private Boolean management;

    @Column(name = "delisting")
    private Boolean delisting;
}