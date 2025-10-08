package com.donttouch.common_service.auth.jwt.info;

public record TokenResponse(String accessToken, String refreshToken) {
    public static TokenResponse of(final String accessToken, final String refreshToken) {
        return new TokenResponse(accessToken, refreshToken);
    }
}
