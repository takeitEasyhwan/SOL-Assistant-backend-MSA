package com.donttouch.internal_assistant_service.domain.member.service;

import com.donttouch.internal_assistant_service.domain.member.entity.Member;
import com.donttouch.internal_assistant_service.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public List<Member> findAllMembers() {

//        List<Member> members = new ArrayList<>();
//        members.add(Member.builder()
//                .id(UUID.randomUUID())
//                .email("sdf@fasf.com")
//                .createdAt(LocalDateTime.now())
//                .password("fasdf")
//                .updatedAt(LocalDateTime.now())
//                .username("hisdf").build());
//
//
//        members.add(Member.builder()
//                .id(UUID.randomUUID())
//                .email("sdavasdc@fasf.com")
//                .createdAt(LocalDateTime.now())
//                .password("vsdac")
//                .updatedAt(LocalDateTime.now())
//                .username("vsadcds").build());

        System.out.println("hi");
        return memberRepository.findAll();
    }
}
