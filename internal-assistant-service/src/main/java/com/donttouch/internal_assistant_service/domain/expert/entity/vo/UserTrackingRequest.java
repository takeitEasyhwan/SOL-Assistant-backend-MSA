package com.donttouch.internal_assistant_service.domain.expert.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserTrackingRequest {

    private List<UserTrackingEvent> events;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class UserTrackingEvent {
        private String userId;
        private String stockId;
        private Double deltaScore;
        private Instant eventTime;
    }
}