package com.donttouch.common_service.auth.repository;


import com.donttouch.common_service.auth.jwt.info.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, String> {
    RefreshToken findByRefreshToken(String refreshToken);
}
