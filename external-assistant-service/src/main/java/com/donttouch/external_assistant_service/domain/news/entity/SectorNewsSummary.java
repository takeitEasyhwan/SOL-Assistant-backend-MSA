package com.donttouch.external_assistant_service.domain.news.entity;

import com.donttouch.common_service.stock.entity.Sector;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sector_news_summary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectorNewsSummary {
    @Id
    @Column(name = "sector_news_summary_id")
    private String sectorNewsSummaryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id", nullable = false)
    private Sector sector;

    @Enumerated(EnumType.STRING)
    @Column(name = "emotion")
    private Emotion emotion;

    @Column(name = "summary")
    private String summary;
}
