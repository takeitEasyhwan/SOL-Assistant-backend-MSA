package com.donttouch.common_service.global.aop;

import com.donttouch.common_service.global.aop.dto.CurrentMemberIdRequest;
import com.donttouch.common_service.global.exception.AccessTokenExpiredException;
import com.donttouch.common_service.global.exception.AuthenticationEntryPointException;
import com.donttouch.common_service.global.exception.InvalidAuthCodeException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class AssignCurrentMemberIdAspect {

    private final HttpServletRequest httpServletRequest;

    @Value("${jwt.secret_key}")
    private String jwtSecret;

    public AssignCurrentMemberIdAspect(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Before("@annotation(com.donttouch.common_service.global.aop.AssignCurrentMemberId)")
    public void assignMemberId(JoinPoint joinPoint) {
        Arrays.stream(joinPoint.getArgs())
                .forEach(arg -> {
                    if (arg instanceof CurrentMemberIdRequest request) {
                        String uuid = extractUuidFromJwt();
                        request.setUserUuid(uuid);
                    }
                });
    }

    private String extractUuidFromJwt() {
        String header = httpServletRequest.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new AuthenticationEntryPointException();
        }

        String token = header.substring(7);

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("USER_ID", String.class);
        } catch (ExpiredJwtException e) {
            throw new AccessTokenExpiredException();
        } catch (JwtException e) {
            throw new InvalidAuthCodeException();
        }catch (Exception e) {
            throw new AuthenticationEntryPointException();
        }

    }

}
