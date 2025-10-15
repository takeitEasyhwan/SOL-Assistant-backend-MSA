package com.donttouch.external_assistant_service.domain.news.entity;

import com.donttouch.common_service.stock.entity.Sector;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "sector_news")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectorNews {

    @Id
    @Column(name = "sector_news_id")
    private String sectorNewsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id", nullable = false)
    private Sector sector;

    @Column(name = "title")
    private String title;

    @Column(name = "date")
    private String date;

    @Column(name = "url")
    private String url;

    @Column(name = "journal")
    private String journal;

    @Column(name = "contents")
    private String contents;
}