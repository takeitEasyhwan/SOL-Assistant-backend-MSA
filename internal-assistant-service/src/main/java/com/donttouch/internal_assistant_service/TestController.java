package com.donttouch.internal_assistant_service;

import com.donttouch.common_service.member.entity.Member;
import com.donttouch.common_service.member.service.MemberService;
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
    public List<Member> hi() {
        return memberService.findAllMembers();
    }
}
