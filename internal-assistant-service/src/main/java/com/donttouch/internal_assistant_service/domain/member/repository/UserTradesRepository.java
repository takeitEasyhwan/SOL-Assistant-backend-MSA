package com.donttouch.internal_assistant_service.domain.member.repository;

import com.donttouch.common_service.stock.entity.Stock;
import com.donttouch.internal_assistant_service.domain.expert.entity.GuruTradeData;
import com.donttouch.internal_assistant_service.domain.member.entity.Side;
import com.donttouch.internal_assistant_service.domain.member.entity.UserTrades;
import com.donttouch.internal_assistant_service.domain.member.entity.vo.TradeHasMonthResponse;
import jakarta.validation.constraints.Null;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserTradesRepository extends JpaRepository<UserTrades, String> {

    @Query("""
        SELECT new com.donttouch.internal_assistant_service.domain.expert.entity.GuruTradeData(
            t.tradeTs,
            SUM(CASE WHEN t.side = 'BUY' THEN t.quantity ELSE 0 END),
            SUM(CASE WHEN t.side = 'SELL' THEN t.quantity ELSE 0 END)
        )
        FROM UserTrades t
        WHERE t.user.id IN :guruUserIds AND t.stock.id = :stockId
        GROUP BY t.tradeTs
        ORDER BY t.tradeTs
    """)
    List<GuruTradeData> aggregateDailyTradeStats(@Param("guruUserIds") List<String> guruUserIds,
                                                 @Param("stockId") String stockId);

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


    List<UserTrades> findByUserId(String userId);

    @Query("""
        SELECT MAX(t.tradeTs)
        FROM UserTrades t
        WHERE t.user.id = :userId
          AND t.stock = :stock
    """)
    LocalDateTime findLatestTradeTimestamp(String userId, Stock stock);

    @Query(value = """
        SELECT DISTINCT DATE_FORMAT(trade_ts, '%Y-%m') AS month
        FROM user_trades
        WHERE user_id = :userId
        ORDER BY month DESC
    """, nativeQuery = true)
    List<String> findDistinctTradeMonths(@Param("userId") String userId);


    List<UserTrades> findByUser_IdAndSide(String userId, Side side);

    @Query("""
    SELECT ut
    FROM UserTrades ut
    WHERE ut.user.id IN :userIds
""")
    List<UserTrades> findByUserIds(@Param("userIds") List<String> userIds);

    @Query(value = """
    SELECT ut.* 
    FROM user_trades ut
    JOIN (
        SELECT DISTINCT DATE(trade_ts) AS trade_date
        FROM user_trades
        ORDER BY trade_date DESC
        LIMIT 2
    ) recent_days ON DATE(ut.trade_ts) = recent_days.trade_date
    WHERE ut.stock_id IN :stockIds
      AND ut.user_id IN :guruUserIds
    """, nativeQuery = true)
    List<UserTrades> findLatestTwoDaysByStockIds(@Param("stockIds") List<String> stockIds,@Param("guruUserIds") List<String> guruUserIds);

}
