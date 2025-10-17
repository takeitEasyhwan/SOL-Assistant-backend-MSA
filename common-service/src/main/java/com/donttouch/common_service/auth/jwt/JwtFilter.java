package com.donttouch.common_service.auth.jwt;

import com.donttouch.common_service.auth.jwt.info.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final String ACCESS_HEADER = "Authorization";
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        log.info("[JwtFilter] Incoming request URI: {}", uri);

        // 모든 헤더 출력 (디버깅용)
        log.info("[JwtFilter] --- Request headers ---");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            log.info("[JwtFilter] {} = {}", name, value);
        }
        log.info("[JwtFilter] ---------------------");

        if (isRequestPassURI(request)) {
            log.info("[JwtFilter] Pass URI (no auth required): {}", uri);
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = getTokenFromHeader(request, ACCESS_HEADER);
        log.info("[JwtFilter] Access Token from header: {}", accessToken != null ? "[PROVIDED]" : "[MISSING]");

        if (!StringUtils.hasText(accessToken)) {
            log.warn("[JwtFilter] Access Token missing for URI: {}", uri);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access Token is missing");
            return;
        }

        if (!tokenProvider.isTokenValid(accessToken)) {
            log.warn("[JwtFilter] Token signature invalid or malformed for URI: {}", uri);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
            return;
        }

        if (tokenProvider.isTokenExpired(accessToken)) {
            log.warn("[JwtFilter] Token expired for URI: {}", uri);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Expired Token");
            return;
        }

        // 인증 성공
        SecurityContextHolder.getContext()
                .setAuthentication(tokenProvider.getAuthentication(accessToken));
        log.info("[JwtFilter] Authentication success for URI: {}", uri);

        filterChain.doFilter(request, response);
    }

    private static boolean isRequestPassURI(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.equals("/") ||
                uri.startsWith("/api/auth") ||
                uri.startsWith("/api/v1/external") ||
                uri.startsWith("/api/v1/internal") ||
                uri.startsWith("/api/exception") ||
                uri.equals("/favicon.ico") ||
                uri.startsWith("/swagger-ui") ||
                uri.startsWith("/v3/api-docs") ||
                uri.equals("/api/v1/internal/register") ||
                uri.equals("/api/v1/internal/hi") ||
                uri.equals("/api/v1/internal/login");
    }

    private String getTokenFromHeader(HttpServletRequest request, String headerName) {
        String token = request.getHeader(headerName);
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }
}
