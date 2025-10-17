package com.donttouch.external_assistant_service.domain.news.entity.vo;

import com.donttouch.external_assistant_service.domain.news.entity.Emotion;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SectorNewsInfoResponse {
    private String sector;
    private Emotion emotion;
    private String summary;
    private List<NewsItem> newsList;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NewsItem {
        private String date;
        private String journal;
        private String title;
        private String url;
    }
}
