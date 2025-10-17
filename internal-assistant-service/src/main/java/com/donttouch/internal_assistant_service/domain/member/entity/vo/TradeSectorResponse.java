package com.donttouch.internal_assistant_service.domain.member.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeSectorResponse {

    private List<SectorDetail> sectorList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SectorDetail {
        private String sectorId;
        private String sectorName;
        private double percentage;
    }
}
