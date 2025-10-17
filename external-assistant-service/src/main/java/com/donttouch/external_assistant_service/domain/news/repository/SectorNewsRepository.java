package com.donttouch.external_assistant_service.domain.news.repository;

import com.donttouch.common_service.sector.entity.Sector;
import com.donttouch.external_assistant_service.domain.news.entity.SectorNews;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface  SectorNewsRepository extends JpaRepository<SectorNews, Long> {
    // 특정 섹터의 뉴스 전체 조회 (최신순)
    List<SectorNews> findBySectorOrderByDateDesc(Sector sector);

    // 최근 뉴스 3개만 가져오기
    List<SectorNews> findTop3BySectorOrderByDateDesc(Sector sector);

    void deleteBySector(Sector sector);

    List<SectorNews> findBySector(Sector sector);
}
