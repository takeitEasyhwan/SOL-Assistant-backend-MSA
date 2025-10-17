package com.donttouch.internal_assistant_service.domain.expert.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "guru_hold")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GuruHold {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "guru_user_id", nullable = false)
    private String guruUserId;
}
