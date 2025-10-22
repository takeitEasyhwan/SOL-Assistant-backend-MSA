package com.donttouch.external_assistant_service.domain.news.service;

import com.donttouch.common_service.sector.entity.Sector;
import com.donttouch.common_service.sector.repository.SectorRepository;
import com.donttouch.common_service.stock.entity.Stock;
import com.donttouch.common_service.stock.repository.StockRepository;
import com.donttouch.common_service.stock.repository.UserStocksRepository;
import com.donttouch.external_assistant_service.domain.exception.ErrorMessage;
import com.donttouch.external_assistant_service.domain.exception.StockNotFoundException;
import com.donttouch.external_assistant_service.domain.news.entity.SectorNews;
import com.donttouch.external_assistant_service.domain.news.entity.SectorNewsSummary;
import com.donttouch.external_assistant_service.domain.news.entity.vo.SectorNewsInfoResponse;
import com.donttouch.external_assistant_service.domain.news.entity.vo.StockMainNewsResponse;
import com.donttouch.external_assistant_service.domain.news.repository.SectorNewsRepository;
import com.donttouch.external_assistant_service.domain.news.repository.SectorNewsSummaryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.PageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MySectorNewsService {

    private final UserStocksRepository userStocksRepository;
    private final SectorNewsRepository sectorNewsRepository;
    private final SectorNewsSummaryRepository sectorNewsSummaryRepository;
    private final StockRepository stockRepository;
    private final SectorRepository sectorRepository;

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

    public StockMainNewsResponse getStockMainNews(String symbol) {
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new StockNotFoundException(ErrorMessage.STOCK_NOT_FOUND));
        SectorNewsSummary sectorNewsSummary = sectorNewsSummaryRepository.findBySector(stock.getSector());
        return StockMainNewsResponse.builder()
                .stockId(stock.getId())
                .sectorId(stock.getSector().getId())
                .emotion(sectorNewsSummary.getEmotion())
                .build();
    }

    @Transactional(readOnly = true)
    public List<SectorNewsInfoResponse> getSectorNewsInfoBySector(String sectorId) {

        List<SectorNewsInfoResponse> result = new ArrayList<>();

        Sector sector = sectorRepository.findById(sectorId)
                .orElseThrow(() -> new EntityNotFoundException("Sector not found with id: " + sectorId));

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
                .emotion(summary != null ? summary.getEmotion() : null)
                .summary(summary != null ? summary.getSummary() : null)
                .newsList(newsItems)
                .build());

        return result;
    }

}