package com.donttouch.internal_assistant_service.global.utils;

import com.donttouch.internal_assistant_service.domain.expert.service.GuruBatchService;
import com.donttouch.internal_assistant_service.domain.expert.service.GuruService;
import lombok.RequiredArgsConstructor;
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

//    @Scheduled
//    public void guruVolumeBatch() {
//        guruBatchService.guruVolumeBatch();
//    }
//
//
//    @Scheduled
//    public void guruTrackingViewBatch() {
//        guruBatchService.guruTrackingViewBatch();
//    }

}