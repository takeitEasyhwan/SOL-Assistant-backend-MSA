package com.donttouch.internal_assistant_service.domain.member.repository;

import com.donttouch.common_service.stock.entity.Stock;
import com.donttouch.internal_assistant_service.domain.member.entity.Side;
import com.donttouch.internal_assistant_service.domain.member.entity.UserTrades;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface UserTradesRepository extends JpaRepository<UserTrades, String> {

    @Query(value = """
        SELECT 
            DATE(t.trade_ts) AS tradeDate,
            SUM(CASE WHEN t.side = 'BUY' THEN t.quantity ELSE 0 END) AS buyVolume,
            SUM(CASE WHEN t.side = 'SELL' THEN t.quantity ELSE 0 END) AS sellVolume
        FROM user_trades t
        WHERE t.user_id IN :userIds
          AND t.stock_id = :stockId
        GROUP BY DATE(t.trade_ts)
        ORDER BY tradeDate ASC
    """, nativeQuery = true)
    List<Map<String, Object>> aggregateGuruTradeDataByDate(
            @Param("userIds") List<String> userIds,
            @Param("stockId") String stockId
    );

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


    @Query(value = """
    SELECT ut.*
    FROM user_trades ut
    WHERE ut.user_id IN (:guruUserIds)
      AND ut.stock_id = :stockId
      AND DATE(ut.trade_ts) IN (
          SELECT trade_date
          FROM (
              SELECT DISTINCT DATE(ut2.trade_ts) AS trade_date
              FROM user_trades ut2
              WHERE ut2.user_id IN (:guruUserIds)
                AND ut2.stock_id = :stockId
              ORDER BY trade_date DESC
              LIMIT 2
          ) recent_dates
      )
    ORDER BY ut.trade_ts DESC
    """, nativeQuery = true)
    List<UserTrades> findLatestTradesByGuruUserIds(@Param("guruUserIds") List<String> guruUserIds, @Param("stockId") String stockId);


    @Query("""
        SELECT SUM(t.price * t.quantity) / SUM(t.quantity)
        FROM UserTrades t
        WHERE t.user.id = :userId
          AND t.stock.id = :stockId
          AND t.side = 'BUY'
    """)
    Double findAverageBuyPriceByUserAndStock(@Param("userId") String userId,
                                             @Param("stockId") String stockId);
}
