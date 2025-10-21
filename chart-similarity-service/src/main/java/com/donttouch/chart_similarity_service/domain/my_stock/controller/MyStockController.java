package com.donttouch.chart_similarity_service.domain.my_stock.controller;

import com.donttouch.chart_similarity_service.domain.my_stock.entity.UserStock;
import com.donttouch.chart_similarity_service.domain.my_stock.service.MyStockService;
import com.donttouch.chart_similarity_service.domain.my_stock.service.SignalService;
import com.donttouch.common_service.global.aop.AssignCurrentMemberId;
import com.donttouch.common_service.global.aop.dto.CurrentMemberIdRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/insight/chart-similarity")
@RequiredArgsConstructor
public class MyStockController {

    private final MyStockService myStockService;
    private final SignalService signalService;

    /**
     * 내 보유 종목 / 시그널 조회 API
     * @param
     * @param signalType "buy" 또는 "sell" (선택)
     */
//    @GetMapping("/my-stock")
//    @AssignCurrentMemberId
//    public ResponseEntity<?> getMyStocks(
//            CurrentMemberIdRequest currentUser,
//            @RequestParam(name = "signal-type", required = false) String signalType
//    ) {
//        String userId = currentUser.getUserUuid();
//        log.info("📩 요청 수신: user_id={}, signal-type={}", userId, signalType);
//
//        if (signalType == null || signalType.isBlank()) {
//            // 기본 보유 종목 조회
//            List<UserStock> stocks = myStockService.getMyStocks(userId);
//            return ResponseEntity.ok(stocks);
//        } else {
//            // signal-type 파라미터 존재 시 → SignalService로 분기
//            var signals = signalService.getSignalWithStockInfo(signalType, userId);
//            return ResponseEntity.ok(signals);
//        }
    @GetMapping("/my-stock")
    @AssignCurrentMemberId
    public ResponseEntity<?> getMyStocks(
            CurrentMemberIdRequest currentUser,
            @RequestParam(name = "signal-type", required = false) String signalType
    ) {
        String userId = currentUser.getUserUuid();
        log.info("📩 요청 수신: user_id={}, signal-type={}", userId, signalType);

        if (signalType == null || signalType.isBlank()) {
            List<UserStock> stocks = myStockService.getMyStocks(userId);
            return ResponseEntity.ok(stocks);
        }

        return signalService.getSignalWithStockInfo(signalType, userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok(Collections.emptyList()));

    }

//}
}
