package com.donttouch.internal_assistant_service.domain.expert.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTrackingResponse {

    private int processedCount;
}
