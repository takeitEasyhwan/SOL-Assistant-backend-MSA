package com.donttouch.external_assistant_service.domain.news.entity.vo;

import com.donttouch.external_assistant_service.domain.news.entity.Emotion;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class StockMainNewsResponse {
    private String stockId;
    private String sectorId;
    private Emotion emotion;
}