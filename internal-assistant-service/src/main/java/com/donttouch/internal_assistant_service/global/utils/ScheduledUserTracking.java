package com.donttouch.internal_assistant_service.global.utils;

import com.donttouch.internal_assistant_service.domain.expert.service.GuruService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledUserTracking {

    private final GuruService guruService;

    @Scheduled(fixedRate = 60000) // 60초 주기
    public void flush() {
        guruService.flushRedisToKafka();
    }
}