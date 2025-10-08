package com.donttouch.common_service.auth.entity.vo;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginRequest {
    private String authId;
    private String password;
}
