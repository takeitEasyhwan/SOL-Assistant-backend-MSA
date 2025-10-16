package com.donttouch.common_service.sector.repository;

import com.donttouch.common_service.sector.entity.Sector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectorRepository extends JpaRepository<Sector, Long> {
    Sector findBySectorName(String sectorName);
}
