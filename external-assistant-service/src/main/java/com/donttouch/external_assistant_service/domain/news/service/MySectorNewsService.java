package com.donttouch.external_assistant_service.domain.news.service;

import com.donttouch.common_service.sector.entity.Sector;
import com.donttouch.common_service.stock.repository.UserStocksRepository;
import com.donttouch.external_assistant_service.domain.news.entity.SectorNews;
import com.donttouch.external_assistant_service.domain.news.entity.SectorNewsSummary;
import com.donttouch.external_assistant_service.domain.news.entity.vo.SectorNewsInfoResponse;
import com.donttouch.external_assistant_service.domain.news.repository.SectorNewsRepository;
import com.donttouch.external_assistant_service.domain.news.repository.SectorNewsSummaryRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MySectorNewsService {

    private final UserStocksRepository userStocksRepository;
    private final SectorNewsRepository sectorNewsRepository;
    private final SectorNewsSummaryRepository sectorNewsSummaryRepository;

    @Transactional
    public List<SectorNewsInfoResponse> getMySectorNewsInfo(String userId) {

        List<Object[]> topSectors = userStocksRepository.findTopSectorsByInvestment(userId, PageRequest.of(0, 3));
        List<SectorNewsInfoResponse> result = new ArrayList<>();

        for (Object[] row : topSectors) {
            Sector sector = (Sector) row[0];

            SectorNewsSummary summary = sectorNewsSummaryRepository.findBySector(sector);

            List<SectorNews> newsList = sectorNewsRepository.findBySector(sector);

            List<SectorNewsInfoResponse.NewsItem> newsItems = newsList.stream()
                    .map(n -> SectorNewsInfoResponse.NewsItem.builder()
                            .date(n.getDate())
                            .journal(n.getJournal())
                            .title(n.getTitle())
                            .url(n.getUrl())
                            .build())
                    .toList();

            result.add(SectorNewsInfoResponse.builder()
                    .sector(sector.getSectorName())
                    .emotion(summary.getEmotion())
                    .summary(summary.getSummary())
                    .newsList(newsItems)
                    .build());
        }

        return result;
    }
}