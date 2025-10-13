package com.donttouch.common_service.stock.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "sectors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Sector {

    @Id
    @Column(name = "sector_id", nullable = false, updatable = false)
    private String id;

    @Column(name = "sector_name", nullable = false)
    private String sectorName;

}