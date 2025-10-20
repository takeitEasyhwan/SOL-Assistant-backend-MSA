package com.donttouch.internal_assistant_service.global.utils;

import com.donttouch.internal_assistant_service.domain.expert.service.GuruBatchService;
import com.donttouch.internal_assistant_service.domain.expert.service.GuruService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledUserTracking {

    private final GuruService guruService;
    private final GuruBatchService guruBatchService;

    @Scheduled(fixedRate = 60000) // 60초 주기
    public void flush() {
        guruService.flushRedisToKafka();
    }

//    @Async
//    @Scheduled(cron = "0 0 6 * * *")
//    public void guruVolumeBatch() {
//        guruBatchService.guruVolumeBatch();
//    }

    @Async
    @Scheduled(fixedRate = 10 * 60 * 1000, initialDelay = 0)
    public void guruTrackingViewBatch() {
        System.out.println("시작전");
        guruBatchService.guruTrackingViewBatch();
        System.out.println("시작후");
    }

//    @Async
//    @Scheduled(fixedRate = 60000)
//    public void userTrackingData() {
//        guruBatchService.userTrackingData();
//    }

}