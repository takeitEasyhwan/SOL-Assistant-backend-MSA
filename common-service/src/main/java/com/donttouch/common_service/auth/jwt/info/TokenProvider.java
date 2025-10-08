package com.donttouch.common_service.auth.jwt.info;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.*;

@Component
public class TokenProvider {

    private static final String AUTH_ID = "AUTH_ID";
    private static final String AUTH_ROLE = "AUTH_ROLE";

    private final String secretKey;
    private final long accessTokenValidityMilliSeconds;
    private final long refreshTokenValidityMilliSeconds;
    private Key secretkey;

    public TokenProvider(
            @Value("${jwt.secret_key}") String secretKey,
            @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValiditySeconds,
            @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValiditySeconds) {

        this.secretKey = secretKey;
        this.accessTokenValidityMilliSeconds = accessTokenValiditySeconds * 1000;
        this.refreshTokenValidityMilliSeconds = refreshTokenValiditySeconds * 1000;
    }

    @PostConstruct
    public void initKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.secretkey = new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    /** ✅ JWT 발급 */
    public TokenResponse createToken(String authId, String role) {
        long now = System.currentTimeMillis();

        Date accessValidity = new Date(now + accessTokenValidityMilliSeconds);
        Date refreshValidity = new Date(now + refreshTokenValidityMilliSeconds);

        Map<String, Object> claims = new HashMap<>();
        claims.put(AUTH_ID, authId);
        claims.put(AUTH_ROLE, role);

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(now))
                .setExpiration(accessValidity)
                .signWith(secretkey, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(now))
                .setExpiration(refreshValidity)
                .signWith(secretkey, SignatureAlgorithm.HS256)
                .compact();

        return TokenResponse.of(accessToken, refreshToken);
    }

    /** ✅ Authentication 추출 */
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretkey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String authId = claims.get(AUTH_ID, String.class);
        String role = claims.get(AUTH_ROLE, String.class);

        List<GrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority(role));

        return new UsernamePasswordAuthenticationToken(authId, null, authorities);
    }

    /** ✅ 토큰 유효성 검사 */
    public boolean validate(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretkey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException |
                 UnsupportedJwtException | IllegalArgumentException e) {
            return false;
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    /** ✅ 만료 여부만 체크 */
    public boolean validateExpire(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretkey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            return false;
        }
    }
}
