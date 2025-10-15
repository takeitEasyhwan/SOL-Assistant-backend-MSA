package com.donttouch.external_assistant_service.domain.news.controller;

import com.donttouch.external_assistant_service.domain.news.entity.SectorNewsSummary;
import com.donttouch.external_assistant_service.domain.news.service.SectorNewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/external/news")
@RequiredArgsConstructor
public class SectorNewsController {

    private final SectorNewsService newsService;

    @PostMapping("/analyze/{sectorName}")
    public SectorNewsSummary analyzeSectorNews(@PathVariable String sectorName) {
        return newsService.processSectorNews(sectorName);
    }

    @PostMapping("/analyze-all")
    public List<SectorNewsSummary> analyzeAllSectorNews() {
        return newsService.processAllSectorNews();
    }

//    @GetMapping("/sector/{sector}")
//    public Object getSectorNews(@PathVariable String sector) {
//        return crawler.crawlSector(sector);
//    }

}