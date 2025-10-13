package com.donttouch.external_assistant_service.domain.chart.repository;

import com.donttouch.common_service.stock.entity.Stock;
import com.donttouch.external_assistant_service.domain.chart.entity.UserTrades;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTradesRepository extends JpaRepository<UserTrades, String> {

}
