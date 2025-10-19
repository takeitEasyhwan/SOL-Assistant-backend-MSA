package com.donttouch.internal_assistant_service.domain.expert.controller;

import com.donttouch.common_service.auth.entity.vo.LoginRequest;
import com.donttouch.common_service.global.aop.AssignCurrentMemberId;
import com.donttouch.common_service.global.aop.dto.CurrentMemberIdRequest;
import com.donttouch.internal_assistant_service.domain.expert.entity.vo.UserTrackingRequest;
import com.donttouch.internal_assistant_service.domain.expert.entity.vo.UserTrackingResponse;
import com.donttouch.internal_assistant_service.domain.expert.service.GuruService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/internal/expert")
@RestController
public class ExpertController {

    private final GuruService guruService;

    @GetMapping("/volume")
    public void volume() {

    }

    @GetMapping("/view")
    public void guruView() {

    }

    @PostMapping("/tracking")
    @AssignCurrentMemberId
    public ResponseEntity<UserTrackingResponse> tracking(@RequestBody UserTrackingRequest userTrackingRequest) {
        UserTrackingResponse userTrackingBatchResponse = guruService.collectBatch(userTrackingRequest.getEvents());
        return new ResponseEntity<>(userTrackingBatchResponse, HttpStatus.CREATED);
    }
}
