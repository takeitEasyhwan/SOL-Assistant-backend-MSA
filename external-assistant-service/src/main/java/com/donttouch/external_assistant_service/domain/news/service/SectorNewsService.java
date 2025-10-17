package com.donttouch.external_assistant_service.domain.news.service;

import com.donttouch.common_service.sector.entity.Sector;
import com.donttouch.common_service.sector.repository.SectorRepository;
import com.donttouch.external_assistant_service.domain.news.entity.SectorNews;
import com.donttouch.external_assistant_service.domain.news.entity.SectorNewsSummary;
import com.donttouch.external_assistant_service.domain.news.repository.SectorNewsRepository;
import com.donttouch.external_assistant_service.domain.news.repository.SectorNewsSummaryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SectorNewsService {
    private final HankyungSectorCrawlerService crawler;
    private final SectorNewsRepository newsRepository;
    private final NewsAnalyzerService analyzer;
    private final SectorNewsSummaryRepository summaryRepository;
    private final SectorRepository sectorRepository;

    @Transactional
    public SectorNewsSummary processSectorNews(String sectorName) {
        Sector sector = sectorRepository.findBySectorName(sectorName);
        newsRepository.deleteBySector(sector);
        summaryRepository.deleteBySector(sector);

        List<SectorNews> newsList = crawler.crawlSector(sectorName);

        newsList.forEach(n -> n.setSector(sector));
        newsRepository.saveAll(newsList);

        SectorNewsSummary summary = analyzer.analyze(sectorName, newsList);
        summary.setSectorNewsSummaryId(UUID.randomUUID().toString());
        summaryRepository.save(summary);
        return summary;
    }

    public List<SectorNewsSummary> processAllSectorNews() {
        List<Sector> allSectors = sectorRepository.findAll();

        List<SectorNewsSummary> result = new ArrayList<>();

        for (Sector sector : allSectors) {
            String sectorName = sector.getSectorName();

            try {
                SectorNewsSummary summary = processSectorNews(sectorName);
                result.add(summary);
            } catch (Exception e) {
                log.error("❌ [{}] 섹터 요약 실패: {}", sectorName, e.getMessage());
            }
        }

        log.info("✅ 전체 섹터 요약 완료: {}개 성공", result.size());
        return result;
    }
}
