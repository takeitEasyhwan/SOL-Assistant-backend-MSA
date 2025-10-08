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

        // ✅ JWT 필터를 건너뛸 URI 설정
        if (isRequestPassURI(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = getTokenFromHeader(request, ACCESS_HEADER);

        if (!StringUtils.hasText(accessToken)) {
            // 토큰이 없으면 그냥 통과 (로그인, 공개 API 등)
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 토큰이 유효하면 인증 처리
            if (tokenProvider.validate(accessToken) && !tokenProvider.validateExpire(accessToken)) {
                SecurityContextHolder.getContext()
                        .setAuthentication(tokenProvider.getAuthentication(accessToken));
            }
        } catch (Exception e) {
            // JWT 파싱 오류 발생 시 로그 남기고 요청 통과 (또는 필요 시 401 응답)
            log.warn("JWT 검증 실패: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private static boolean isRequestPassURI(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.equals("/") ||
                uri.startsWith("/api/auth") ||
                uri.startsWith("/api/exception") ||
                uri.startsWith("/api/fortune-cookie") ||
                uri.equals("/favicon.ico") ||
                uri.startsWith("/swagger-ui") ||
                uri.startsWith("/v3/api-docs") ||
                uri.equals("/hi") ||
                uri.equals("/api/v1/register") ||
                uri.equals("/api/v1/login");
    }

    private String getTokenFromHeader(HttpServletRequest request, String headerName) {
        String token = request.getHeader(headerName);
        if (StringUtils.hasText(token)) {
            // Bearer 접두어 제거 가능
            if (token.startsWith("Bearer ")) {
                return token.substring(7);
            }
            return token;
        }
        return null;
    }
}
