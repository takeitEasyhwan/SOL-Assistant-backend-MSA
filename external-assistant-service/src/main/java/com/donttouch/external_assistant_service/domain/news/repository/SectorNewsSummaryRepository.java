package com.donttouch.external_assistant_service.domain.news.repository;

import com.donttouch.common_service.stock.entity.Sector;
import com.donttouch.external_assistant_service.domain.news.entity.SectorNewsSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SectorNewsSummaryRepository extends JpaRepository<SectorNewsSummary, Long> {
    // 특정 섹터의 최신 요약 결과 1건 조회
    Optional<SectorNewsSummary> findTopBySectorOrderBySectorNewsSummaryIdDesc(Sector sector);

    void deleteBySector(Sector sector);

    SectorNewsSummary findBySector(Sector sector);
}
