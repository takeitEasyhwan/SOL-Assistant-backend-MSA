package com.donttouch.external_assistant_service.domain.chart.repository;

import com.donttouch.common_service.auth.entity.vo.InvestmentType;
import com.donttouch.common_service.guru.entity.GuruDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuruDayTradeRepository extends JpaRepository<GuruDay, Long> {
    @Query("SELECT g.guruUserId FROM GuruDay g")
    List<String> findAllUserIds();
}