package com.donttouch.internal_assistant_service.global.utils;

import com.donttouch.internal_assistant_service.domain.expert.entity.UserTracking;
import com.donttouch.internal_assistant_service.domain.expert.repository.UserTrackingRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserTrackingConsumer {

    private final UserTrackingRepository trackingRepository;

    @KafkaListener(
            topics = "user-tracking-topic",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeBatch(List<ConsumerRecord<String, Map<String, Object>>> records) {

        List<UserTracking> entities = records.stream()
                .map(record -> {
                    Map<String, Object> map = record.value(); // value 꺼내기
                    return new UserTracking(
                            UUID.randomUUID().toString(),
                            (String) map.get("userId"),
                            (String) map.get("stockId"),
                            ((Number) map.get("score")).doubleValue(),
                            Instant.now(),
                            Instant.now()
                    );
                })
                .collect(Collectors.toList());

        trackingRepository.saveAll(entities);
    }
}
