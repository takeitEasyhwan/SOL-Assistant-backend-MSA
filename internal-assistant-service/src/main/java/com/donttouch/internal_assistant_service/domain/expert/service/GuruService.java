package com.donttouch.internal_assistant_service.domain.expert.service;

import com.donttouch.common_service.auth.entity.User;
import com.donttouch.common_service.auth.entity.vo.MyInfoResponse;
import com.donttouch.common_service.auth.repository.UserRepository;
import com.donttouch.common_service.global.aop.dto.CurrentMemberIdRequest;
import com.donttouch.internal_assistant_service.domain.expert.entity.vo.UserTrackingRequest;
import com.donttouch.internal_assistant_service.domain.expert.entity.vo.UserTrackingResponse;
import com.donttouch.internal_assistant_service.domain.member.exception.ErrorMessage;
import com.donttouch.internal_assistant_service.domain.member.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuruService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final StringRedisTemplate trackingRedisTemplate;
    private final UserRepository userRepository;

    private static final String KEY_PREFIX = "ut:USER:";

    public UserTrackingResponse collectBatch(List<UserTrackingRequest.UserTrackingEvent> events) {

        Map<String, Double> agg = events.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getUserId() + ":" + e.getStockId(),
                        Collectors.summingDouble(UserTrackingRequest.UserTrackingEvent::getDeltaScore)
                ));

        agg.forEach((key, value) -> {
            String redisKey = KEY_PREFIX + key;
            trackingRedisTemplate.opsForValue().increment(redisKey, value);
            trackingRedisTemplate.expire(redisKey, 1, TimeUnit.DAYS);
        });

        return UserTrackingResponse.builder()
                .processedCount(events.size())
                .build();
    }

    public void flushRedisToKafka() {
        trackingRedisTemplate.keys(KEY_PREFIX + "*").forEach(redisKey -> {
            String scoreStr = trackingRedisTemplate.opsForValue().get(redisKey);
            if (scoreStr != null) {
                Double score = Double.valueOf(scoreStr);
                trackingRedisTemplate.delete(redisKey);

                String[] parts = redisKey.replace(KEY_PREFIX, "").split(":");
                String userId = parts[0];
                String stockId = parts[1];

                kafkaTemplate.send("user-tracking-topic", userId + ":" + stockId, Map.of(
                        "userId", userId,
                        "stockId", stockId,
                        "score", score
                ));
            }
        });
    }


    public MyInfoResponse getMyInfo(CurrentMemberIdRequest currentMemberIdRequest) {

        User user = userRepository.findById(currentMemberIdRequest.getUserUuid()).
                orElseThrow(()-> new UserNotFoundException(ErrorMessage.USER_NOT_FOUND));

        return MyInfoResponse.builder()
                .id(user.getId())
                .investmentType(user.getInvestmentType())
                .name(user.getName())
                .build();
    }
}
