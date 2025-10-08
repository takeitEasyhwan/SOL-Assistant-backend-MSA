package com.donttouch.common_service.auth.repository;

import com.donttouch.common_service.auth.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<UserAuth, Long> {
    UserAuth findByAuthId(String authId);
}
