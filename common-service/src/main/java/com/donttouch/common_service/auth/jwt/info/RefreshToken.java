package com.donttouch.common_service.auth.jwt.info;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "refresh", timeToLive = 604800) // 7일
public class RefreshToken {

    private String userId; // DB의 UUID PK 매핑 가능
    @Indexed
    private String authId; // 로그인용 ID (username)

    private Collection<? extends GrantedAuthority> authorities;

    @Id
    private String refreshToken;

    public String getAuthority() {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList())
                .get(0);
    }
}
