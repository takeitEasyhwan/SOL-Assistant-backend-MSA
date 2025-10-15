package com.donttouch.chart_similarity_service.domain.my_stock.repository;

import com.donttouch.chart_similarity_service.domain.my_stock.entity.UserStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserStockRepository extends JpaRepository<UserStock, Long> {
    List<UserStock> findByUserId(String userId);
}
