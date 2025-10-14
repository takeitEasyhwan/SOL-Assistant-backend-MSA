package com.donttouch.external_assistant_service.domain.chart.repository;

import com.donttouch.common_service.guru.entity.GuruHold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface GuruHoldTradeRepository extends JpaRepository<GuruHold, Long> {
    @Query("SELECT g.guruUserId FROM GuruHold g")
    List<String> findAllUserIds();
}