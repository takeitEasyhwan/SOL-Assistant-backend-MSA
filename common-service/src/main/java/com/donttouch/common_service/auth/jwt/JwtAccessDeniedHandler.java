package com.donttouch.common_service.auth.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private static final String EXCEPTION_ACCESS_HANDLER = "/api/v1/internal/exception/access-denied";

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        if (!request.isSecure()) {
            String redirectUrl =
                    "https://" + request.getServerName() + EXCEPTION_ACCESS_HANDLER;
            response.sendRedirect(redirectUrl);
        } else {
            response.sendRedirect(EXCEPTION_ACCESS_HANDLER);
        }
    }

}
