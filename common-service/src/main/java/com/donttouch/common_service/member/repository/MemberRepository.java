package com.donttouch.common_service.member.repository;


import com.donttouch.common_service.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
    // 필요 시 커스텀 쿼리 추가 가능
}