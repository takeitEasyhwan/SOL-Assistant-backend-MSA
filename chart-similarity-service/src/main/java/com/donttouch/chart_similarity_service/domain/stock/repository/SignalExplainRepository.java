package com.donttouch.chart_similarity_service.domain.stock.repository;

import com.donttouch.chart_similarity_service.domain.stock.entity.SignalExplain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SignalExplainRepository extends JpaRepository<SignalExplain, Long> {
}
