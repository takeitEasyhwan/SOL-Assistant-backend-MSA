package com.donttouch.internal_assistant_service.domain.member.repository;

import com.donttouch.internal_assistant_service.domain.member.entity.HoldingPeriodDistribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HoldingPeriodDistributionRepository extends JpaRepository<HoldingPeriodDistribution, Long> {

    Optional<HoldingPeriodDistribution> findByQuantile(Double quantile);
    List<HoldingPeriodDistribution> findAllByOrderByQuantileAsc();
}