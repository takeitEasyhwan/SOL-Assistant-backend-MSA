package com.donttouch.external_assistant_service.domain.chart.repository;

import com.donttouch.external_assistant_service.domain.chart.entity.GuruTradeData;
import com.donttouch.external_assistant_service.domain.chart.entity.UserTrades;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface UserTradesRepository extends JpaRepository<UserTrades, String> {
    @Query("""
        SELECT new com.donttouch.external_assistant_service.domain.chart.entity.GuruTradeData(
            t.tradeTs,
            SUM(CASE WHEN t.side = 'BUY' THEN t.quantity ELSE 0 END),
            SUM(CASE WHEN t.side = 'SELL' THEN t.quantity ELSE 0 END)
        )
        FROM UserTrades t
        WHERE t.user.id IN :guruUserIds AND t.stock.id = :stockId
        GROUP BY t.tradeTs
        ORDER BY t.tradeTs
    """)
    List<GuruTradeData> aggregateDailyTradeStats(@Param("guruUserIds") List<String> guruUserIds, @Param("stockId") String stockId);

    @Query("""
        SELECT SUM(us.quantity)
        FROM UserStocks us
        WHERE us.user.id IN :guruUserIds AND us.stock.id = :stockId
    """)
    Double sumTotalHoldings(@Param("guruUserIds") List<String> guruUserIds, @Param("stockId") String stockId);
}
