package com.donttouch.internal_assistant_service.domain.auth.controller;

import com.donttouch.internal_assistant_service.domain.auth.vo.LoginRequest;
import com.donttouch.internal_assistant_service.domain.auth.vo.LoginResponse;
import jdk.jshell.Snippet;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        LoginResponse testLoginResponse = LoginResponse.builder().build();
        return new ResponseEntity<>(testLoginResponse, HttpStatus.ACCEPTED);
    }
}
