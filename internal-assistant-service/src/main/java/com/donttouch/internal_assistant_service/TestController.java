package com.donttouch.internal_assistant_service;

import com.donttouch.common_service.global.aop.AssignCurrentMemberId;
import com.donttouch.internal_assistant_service.domain.member.service.MemberService;
import com.donttouch.internal_assistant_service.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final MemberService memberService;

    @GetMapping("/hi")
    @Transactional
    @AssignCurrentMemberId
    public List<Member> hi() {
        List<Member> memberList = memberService.findAllMembers();
        for (Member member : memberList) {
            System.out.println(member.toString());
        }
        System.out.println("이게무슨일이오!");

        return memberList;
    }
}
