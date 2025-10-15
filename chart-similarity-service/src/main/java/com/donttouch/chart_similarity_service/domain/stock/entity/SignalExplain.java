package com.donttouch.chart_similarity_service.domain.stock.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "signal_explain")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignalExplain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "signal_id")  // ✅ 여기 수정
    private Long signalId;

    @Column(name = "signal_name")
    private String signalName;

    @Column(name = "signal_type")
    private String signalType;

    @Column(name = "signal_category")
    private String signalCategory;

    @Column(name = "description")
    private String description;

    @Column(name = "description_detail", columnDefinition = "TEXT")
    private String descriptionDetail;

    @Column(name = "created_at")
    private String createdAt;
}
