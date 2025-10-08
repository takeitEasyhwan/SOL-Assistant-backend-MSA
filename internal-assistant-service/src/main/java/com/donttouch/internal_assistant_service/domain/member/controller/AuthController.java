package com.donttouch.internal_assistant_service.domain.member.controller;

import com.donttouch.common_service.auth.entity.vo.LoginRequest;
import com.donttouch.common_service.auth.entity.vo.LoginResponse;
import com.donttouch.common_service.auth.jwt.info.TokenResponse;
import com.donttouch.common_service.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인", description = "로그인을 합니다.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        System.out.println(request.getAuthId() + ":" + request.getPassword());
        TokenResponse tokenResponse = authService.login(request.getAuthId(), request.getPassword());
        return new ResponseEntity<>(LoginResponse.of(
                tokenResponse.accessToken(),
                tokenResponse.refreshToken()
        ), HttpStatus.OK);
    }

    @GetMapping("/reissue")
    public ResponseEntity<LoginResponse> reissueToken(HttpServletRequest request) {
        TokenResponse tokenResponse = authService.reissueAccessToken(request);
        return new ResponseEntity<>(LoginResponse.of(
                tokenResponse.accessToken(),
                tokenResponse.refreshToken()
        ), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody LoginRequest request) {
        TokenResponse tokenResponse = authService.register(request.getAuthId(), request.getPassword());
        return ResponseEntity.ok(
                LoginResponse.of(tokenResponse.accessToken(), tokenResponse.refreshToken())
        );
    }

}