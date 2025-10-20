package com.donttouch.common_service.stock.repository;

import com.donttouch.common_service.auth.entity.User;
import com.donttouch.common_service.stock.entity.Stock;
import com.donttouch.common_service.stock.entity.UserStocks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserStocksRepository extends JpaRepository<UserStocks, String> {

    @Query("""
    SELECT us.stock.sector AS sector,
           SUM(us.quantity * us.costBasis) AS totalInvestment
    FROM UserStocks us
    WHERE us.user.id = :userId
    GROUP BY us.stock.sector
    ORDER BY totalInvestment DESC
    """)
    List<Object[]> findTopSectorsByInvestment(@Param("userId") String userId, Pageable pageable);

    Optional<UserStocks> findByUserAndStock(User user, Stock stock);

    @Query("""
        SELECT us
        FROM UserStocks us
        JOIN FETCH us.stock s
        WHERE us.user.id = :userId
    """)
    List<UserStocks> findByUserId(@Param("userId") String userId);

    @Query("""
    SELECT SUM(us.quantity)
    FROM UserStocks us
    WHERE us.user.id IN :guruUserIds
    AND us.stock.id = :stockId
""")
    Double sumTotalHoldings(
            @Param("guruUserIds") List<String> guruUserIds,  // UUID로 변경
            @Param("stockId") String stockId                 // UUID로 변경
    );

}