//package com.donttouch.chart_similarity_service.domain.my_stock.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//@Entity
//@Table(name = "user_stocks")
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class UserStock {
//
//    @Id
//    @Column(name = "user_stocks_id")
//    private String userStocksId;
//
//    @Column(name = "stock_id", nullable = false)
//    private String stockId;
//
//    @Column(name = "user_id", nullable = false)
//    private String userId;
//
//    @Column(nullable = false)
//    private Double quantity;
//
//    @Column(name = "cost_basis", nullable = false)
//    private Double costBasis;
//}


package com.donttouch.chart_similarity_service.domain.my_stock.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "signal_buy")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignalBuy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stockId;
    private String signalName;
}
