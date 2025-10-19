package com.donttouch.external_assistant_service.domain.news.entity.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SectorAnalysisResponse {
    private String sector;
    private String analysis;
    private String sentiment;
}