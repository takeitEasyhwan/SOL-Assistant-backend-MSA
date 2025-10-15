package com.donttouch.common_service.stock.repository;

import com.donttouch.common_service.stock.entity.Sector;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SectorRepository extends JpaRepository<Sector, Long> {
    Sector findBySectorName(String sectorName);
}
