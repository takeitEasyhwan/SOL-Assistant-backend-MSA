//package com.donttouch.external_assistant_service.domain.news.scheduler;
//
//import com.donttouch.external_assistant_service.domain.news.service.SectorNewsService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class SectorNewsScheduler {
//    private final SectorNewsService sectorNewsService;
//
//    /**
//     * 매일 오전 5시에 전체 섹터 뉴스 요약 실행
//     * cron 형식: 초 분 시 일 월 요일
//     * 예) "0 0 5 * * *" → 매일 5시 정각
//     */
//    @Scheduled(cron = "0 0 5 * * *", zone = "Asia/Seoul")
//    public void runDailySectorNewsBatch() {
//        log.info("🚀 [Batch] 섹터 뉴스 요약 배치 시작");
//        try {
//            var result = sectorNewsService.processAllSectorNews();
//            log.info("✅ [Batch] 섹터 뉴스 요약 완료: {}개 성공", result.size());
//        } catch (Exception e) {
//            log.error("❌ [Batch] 섹터 뉴스 요약 실패: {}", e.getMessage(), e);
//        }
//    }
//}
