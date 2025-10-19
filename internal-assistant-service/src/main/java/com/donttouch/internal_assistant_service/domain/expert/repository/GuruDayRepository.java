package com.donttouch.internal_assistant_service.domain.expert.repository;

import com.donttouch.internal_assistant_service.domain.expert.entity.GuruDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuruDayRepository extends JpaRepository<GuruDay, Long> {
    @Query("SELECT g.guruUserId FROM GuruDay g")
    List<String> findAllUserIds();
}