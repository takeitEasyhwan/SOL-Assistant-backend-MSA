package com.donttouch.internal_assistant_service.domain.member.repository;

import com.donttouch.internal_assistant_service.domain.expert.entity.GuruTradeData;
import com.donttouch.internal_assistant_service.domain.member.entity.Side;
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
    SELECT new com.donttouch.internal_assistant_service.domain.expert.entity.GuruTradeData(
        t.tradeTs,
        COALESCE(SUM(CASE WHEN t.side = 'BUY' THEN t.quantity ELSE 0 END) * 1.0, 0.0),
        COALESCE(SUM(CASE WHEN t.side = 'SELL' THEN t.quantity ELSE 0 END) * 1.0, 0.0)
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


    List<UserTrades> findByUser_IdAndSide(String userId, Side side);

    @Query("""
    SELECT ut
    FROM UserTrades ut
    WHERE ut.user.id IN :userIds
""")
    List<UserTrades> findByUserIds(@Param("userIds") List<String> userIds);

}
