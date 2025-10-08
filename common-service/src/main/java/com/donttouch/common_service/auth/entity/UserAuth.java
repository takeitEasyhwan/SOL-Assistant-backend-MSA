package com.donttouch.common_service.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_auth")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserAuth {

    @Id
    @Column(name = "user_auth_id", updatable = false, nullable = false)
    private String userAuthId; // PK, 자동 생성

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // users 테이블 참조

    @Column(name = "auth_id", nullable = false, unique = true)
    private String authId;    // 로그인용 ID

    @Column(name = "password", nullable = false)
    private String password;  // BCrypt 해시

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    // insert 전에 UUID 자동 생성
    @PrePersist
    public void prePersist() {
        if (this.userAuthId == null) {
            this.userAuthId = UUID.randomUUID().toString();
        }
    }
}
