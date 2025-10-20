package com.donttouch.internal_assistant_service.domain.expert.repository;

import com.donttouch.internal_assistant_service.domain.expert.entity.UserTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserTrackingRepository extends JpaRepository<UserTracking, Long> {


    List<UserTracking> findByUserIdIn(List<String> guruUserIds);
}
