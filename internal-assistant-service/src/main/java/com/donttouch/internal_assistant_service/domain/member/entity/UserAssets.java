package com.donttouch.internal_assistant_service.domain.member.entity;

import com.donttouch.common_service.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_assets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAssets {

    @Id
    @Column(name = "user_asset_id", nullable = false, unique = true)
    private String userAssetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "total_balance", nullable = false)
    private Double totalBalance;

    @Column(name = "principal", nullable = false)
    private Double principal;
}
