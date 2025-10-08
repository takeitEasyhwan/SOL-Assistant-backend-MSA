package com.donttouch.common_service.auth.entity.vo;

import com.donttouch.common_service.auth.jwt.info.TokenResponse;
import lombok.Getter;

@Getter
public class LoginResponse {

    private final TokenResponse tokenResponse;

    private LoginResponse(final String accessToken, final String refreshToken) {
        this.tokenResponse = new TokenResponse(accessToken, refreshToken);
    }

    public static LoginResponse of(final String accessToken, final String refreshToken) {
        return new LoginResponse(accessToken, refreshToken);
    }
}
