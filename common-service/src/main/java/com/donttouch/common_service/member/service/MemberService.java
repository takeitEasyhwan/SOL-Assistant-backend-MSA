package com.donttouch.common_service.member.service;

import com.donttouch.common_service.member.entity.Member;
import com.donttouch.common_service.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public List<Member> findAllMembers() {
        return memberRepository.findAll();
    }
}
