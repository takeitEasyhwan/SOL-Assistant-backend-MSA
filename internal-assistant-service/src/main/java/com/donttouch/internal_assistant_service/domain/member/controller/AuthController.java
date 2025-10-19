package com.donttouch.internal_assistant_service.domain.member.controller;

import com.donttouch.common_service.auth.entity.vo.LoginRequest;
import com.donttouch.common_service.auth.entity.vo.LoginResponse;
import com.donttouch.common_service.auth.entity.vo.MyInfoResponse;
import com.donttouch.common_service.auth.jwt.info.TokenResponse;
import com.donttouch.common_service.auth.service.AuthService;
import com.donttouch.common_service.auth.entity.vo.RegisterRequest;
import com.donttouch.common_service.global.aop.AssignCurrentMemberId;
import com.donttouch.common_service.global.aop.dto.CurrentMemberIdRequest;
import com.donttouch.internal_assistant_service.domain.expert.service.GuruService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internal")
public class AuthController {

    private final AuthService authService;
    private final GuruService guruService;

    @Operation(summary = "로그인", description = "로그인을 합니다.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
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
    public ResponseEntity<LoginResponse> register(@RequestBody RegisterRequest registerRequest) {
        TokenResponse tokenResponse = authService.register(registerRequest);
        return ResponseEntity.ok(
LoginResponse.of(tokenResponse.accessToken(), tokenResponse.refreshToken())
        );
    }

    @GetMapping("/my-info")
    @AssignCurrentMemberId
    public ResponseEntity<MyInfoResponse> myInfo(CurrentMemberIdRequest currentMemberIdRequest) {
        MyInfoResponse myInfoResponse = guruService.getMyInfo(currentMemberIdRequest);
        return new ResponseEntity<>(myInfoResponse, HttpStatus.OK);
    }

}