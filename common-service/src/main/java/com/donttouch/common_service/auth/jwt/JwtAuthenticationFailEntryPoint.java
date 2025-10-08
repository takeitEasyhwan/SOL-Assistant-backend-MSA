package com.donttouch.common_service.auth.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationFailEntryPoint implements AuthenticationEntryPoint {
    private static final String EXCEPTION_ENTRY_POINT = "/login/exception/entry-point";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        if (!request.isSecure()) {
            String redirectUrl =
                    "https://" + request.getServerName() + EXCEPTION_ENTRY_POINT;
            response.sendRedirect(redirectUrl);
        } else {
            response.sendRedirect(EXCEPTION_ENTRY_POINT);
        }
    }
}
