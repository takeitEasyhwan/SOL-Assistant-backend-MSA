package com.donttouch.internal_assistant_service.domain.expert.repository;

import com.donttouch.internal_assistant_service.domain.expert.entity.GuruSwing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuruSwingRepository extends JpaRepository<GuruSwing, Long> {
    @Query("SELECT g.guruUserId FROM GuruSwing g")
    List<String> findAllUserIds();
}