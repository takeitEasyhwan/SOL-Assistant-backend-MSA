package com.donttouch.internal_assistant_service.domain.expert.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_tracking")
public class UserTracking {

    @Id
    private String userTrackingId;
    private String userId;
    private String stockId;
    private Double score;
    private Instant eventTime;
    private Instant persistedAt;

}
