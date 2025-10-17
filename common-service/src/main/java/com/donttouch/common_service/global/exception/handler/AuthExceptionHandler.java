package com.donttouch.common_service.global.exception.handler;

import com.donttouch.common_service.global.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class AuthExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<String> accessDeniedException(HttpServletRequest request) {
        return new ResponseEntity<>(
                "{\"message\":\"접근 불가능한 권한입니다.\"}",
                corsHeaders(request),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(AuthenticationEntryPointException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<String> authenticationEntryPointException(HttpServletRequest request) {
        return new ResponseEntity<>(
                "{\"message\":\"로그인이 필요한 요청 입니다.\"}",
                corsHeaders(request),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(InvalidAuthCodeException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<String> invalidAuthCodeException(HttpServletRequest request) {
        return new ResponseEntity<>(
                "{\"message\":\"유효하지 않은 인가 코드입니다.\"}",
                corsHeaders(request),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(AccessTokenExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<String> accessTokenExpiredException(HttpServletRequest request) {
        return new ResponseEntity<>(
                "{\"message\":\"액세스 토큰이 만료되었습니다.\"}",
                corsHeaders(request),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(ReissueFailException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<String> reissueFailException(HttpServletRequest request) {
        return new ResponseEntity<>(
                "{\"message\":\"리프레시 토큰이 올바르지 않습니다. 다시 로그인해주세요.\"}",
                corsHeaders(request),
                HttpStatus.UNAUTHORIZED
        );
    }

    private HttpHeaders corsHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", request.getHeader("Origin"));
        headers.set("Access-Control-Allow-Credentials", "true");
        headers.set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        headers.set("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization");
        return headers;
    }

}
