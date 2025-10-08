package com.donttouch.common_service.auth.service;

import com.donttouch.common_service.auth.entity.User;
import com.donttouch.common_service.auth.entity.UserAuth;
import com.donttouch.common_service.auth.jwt.info.RefreshToken;
import com.donttouch.common_service.auth.jwt.info.TokenProvider;
import com.donttouch.common_service.auth.jwt.info.TokenResponse;
import com.donttouch.common_service.auth.repository.AuthRepository;
import com.donttouch.common_service.auth.repository.RefreshTokenRedisRepository;
import com.donttouch.common_service.auth.repository.UserRepository;
import com.donttouch.common_service.global.exception.ReissueFailException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String REFRESH_HEADER = "RefreshToken";
    private static final String DEFAULT_ROLE = "ROLE_USER";

    private final TokenProvider tokenProvider;
    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final PasswordEncoder passwordEncoder;

    /** 회원 로그인 */
    public TokenResponse login(String authId, String password) {
        System.out.println("[login] authId: " + authId);

        UserAuth userAuth = authRepository.findByAuthId(authId);
        if (userAuth == null) {
            System.out.println("[login] 존재하지 않는 사용자");
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        if (!passwordEncoder.matches(password, userAuth.getPassword())) {
            System.out.println("[login] 비밀번호 불일치");
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        TokenResponse tokenResponse = tokenProvider.createToken(authId, DEFAULT_ROLE);
        System.out.println("[login] accessToken: " + tokenResponse.accessToken());
        System.out.println("[login] refreshToken: " + tokenResponse.refreshToken());

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(String.valueOf(userAuth.getUserAuthId()))
                .authId(authId)
                .refreshToken(tokenResponse.refreshToken())
                .authorities(List.of(new SimpleGrantedAuthority(DEFAULT_ROLE)))
                .build();
        refreshTokenRedisRepository.save(refreshToken);
        System.out.println("[login] refreshToken saved in Redis");

        userAuth.setLastLogin(LocalDateTime.now());
        authRepository.save(userAuth);

        return tokenResponse;
    }

    /** 회원 없으면 생성 후 반환 */
    @Transactional
    public UserAuth saveIfNonExist(String authId, String rawPassword) {
        UserAuth userAuth = authRepository.findByAuthId(authId);
        if (userAuth != null) return userAuth;

        UserAuth newUser = UserAuth.builder()
                .userAuthId(String.valueOf(UUID.randomUUID()))
                .authId(authId)
                .user(userAuth.getUser())
                .password(passwordEncoder.encode(rawPassword))
                .lastLogin(LocalDateTime.now())
                .build();

        return authRepository.save(newUser);
    }

    /** Refresh Token 저장 */
    private void saveRefreshToken(UserAuth userAuth, TokenResponse tokenResponse, String role) {
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(String.valueOf(userAuth.getUserAuthId()))
                .authId(userAuth.getAuthId())
                .authorities(List.of(new SimpleGrantedAuthority(role)))
                .refreshToken(tokenResponse.refreshToken())
                .build();

        refreshTokenRedisRepository.save(refreshToken);
    }

    /** Refresh Token으로 Access Token 재발급 */
    public TokenResponse reissueAccessToken(HttpServletRequest request) {
        String refreshToken = request.getHeader(REFRESH_HEADER);
        if (!StringUtils.hasText(refreshToken)) throw new ReissueFailException();

        RefreshToken findToken = refreshTokenRedisRepository.findByRefreshToken(refreshToken);
        if (findToken == null || !tokenProvider.validate(refreshToken)) {
            throw new ReissueFailException();
        }

        String role = "ROLE_USER";
        TokenResponse tokenResponse = tokenProvider.createToken(findToken.getAuthId(), role);
        saveRefreshToken(UserAuth.builder()
                .userAuthId(findToken.getUserId())
                .authId(findToken.getAuthId())
                .build(), tokenResponse, role);

        return tokenResponse;
    }
    /** 회원가입 + 토큰 발급 */
    @Transactional
    public TokenResponse register(String authId, String rawPassword) {
        System.out.println("[register] authId: " + authId);

        // 이미 존재하는지 확인
        UserAuth existingUser = authRepository.findByAuthId(authId);
        if (existingUser != null) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }

        // 1️⃣ User 엔티티 생성
        User user = User.builder()
                .id(UUID.randomUUID().toString()) // PK
                .build();
        userRepository.save(user); // UserRepository 필요

        // 2️⃣ UserAuth 생성
        UserAuth newUserAuth = UserAuth.builder()
                .userAuthId(UUID.randomUUID().toString())
                .authId(authId)
                .password(passwordEncoder.encode(rawPassword))
                .lastLogin(LocalDateTime.now())
                .user(user) // FK 연결
                .build();

        authRepository.save(newUserAuth);
        System.out.println("[register] new userAuth saved");

        // 3️⃣ 토큰 발급
        TokenResponse tokenResponse = tokenProvider.createToken(authId, DEFAULT_ROLE);
        saveRefreshToken(newUserAuth, tokenResponse, DEFAULT_ROLE);

        return tokenResponse;
    }


}
