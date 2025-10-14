package com.donttouch.common_service.guru.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "guru_swing")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GuruSwing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "guru_user_id", nullable = false)
    private String guruUserId;
}