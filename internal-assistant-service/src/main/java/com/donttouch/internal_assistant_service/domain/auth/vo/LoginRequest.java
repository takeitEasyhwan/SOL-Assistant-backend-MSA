package com.donttouch.internal_assistant_service.domain.auth.vo;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class LoginRequest {
    private String username;
    private String password;
}
