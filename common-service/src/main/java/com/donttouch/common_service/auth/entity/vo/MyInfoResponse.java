package com.donttouch.common_service.auth.entity.vo;

import com.donttouch.common_service.auth.entity.InvestmentType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyInfoResponse {
    @Id
    @Column(name = "user_id", updatable = false, nullable = false)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "investment_type")
    private InvestmentType investmentType;

    @Column(name = "name")
    private String name;

}
