package com.donttouch.internal_assistant_service.domain.member.repository;

import com.donttouch.internal_assistant_service.domain.member.entity.GuruTradeData;
import com.donttouch.internal_assistant_service.domain.member.entity.UserTrades;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserTradesRepository extends JpaRepository<UserTrades, String> {
    @Query("""
        SELECT new com.donttouch.internal_assistant_service.domain.member.entity.GuruTradeData(
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
        SELECT t
        FROM UserTrades t
        WHERE t.user.id = :userId
          AND t.tradeTs BETWEEN :start AND :end
        ORDER BY t.tradeTs DESC
    """)
    List<UserTrades> findByUserIdAndTradeTsBetween(
            @Param("userId") String userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
