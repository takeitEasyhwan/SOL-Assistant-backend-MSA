package com.donttouch.internal_assistant_service.domain.member.repository;

import com.donttouch.internal_assistant_service.domain.member.entity.UserAssets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface UserAssetsRepository extends JpaRepository<UserAssets, Long> {

    @Query("""
        SELECT ua
        FROM UserAssets ua
        WHERE ua.user.id = :userId
    """)
    Optional<UserAssets> findByUserId(@Param("userId") String userId);
}