package com.donttouch.external_assistant_service.domain.news.service;

import com.donttouch.common_service.sector.entity.Sector;
import com.donttouch.external_assistant_service.domain.news.entity.Emotion;
import com.donttouch.external_assistant_service.domain.news.entity.SectorNews;
import com.donttouch.external_assistant_service.domain.news.entity.SectorNewsSummary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class NewsAnalyzerService {

    public SectorNewsSummary analyze(String sectorName, List<SectorNews> newsList) {
        Sector sector = newsList.get(0).getSector();


        String summaryText = "아직은 요약이 안됩니다ㅠㅠ";

        return SectorNewsSummary.builder()
                .sector(sector)
                .emotion(Emotion.POSITIVE)
                .summary(summaryText)
                .build();
    }
}
