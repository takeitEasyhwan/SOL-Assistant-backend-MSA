//package com.donttouch.chart_similarity_service.domain.my_stock.controller;
//
//import com.donttouch.chart_similarity_service.domain.my_stock.entity.UserStock;
//import com.donttouch.chart_similarity_service.domain.my_stock.service.MyStockService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@Slf4j
//@RestController
//@RequestMapping("/api/v1/chart-similarity")
//@RequiredArgsConstructor
//public class MyStockController {
//
//    private final MyStockService myStockService;
//
//    /**
//     * 내 보유 종목 조회 API
//     * @param userId 사용자 UUID
//     * @param signalType "buy" 또는 "sell" (선택)
//     */
//    @GetMapping("/my-stock")
//    public ResponseEntity<List<UserStock>> getMyStocks(
//            @RequestParam("user_id") String userId,
//            @RequestParam(name = "signal-type", required = false) String signalType
//    ) {
//        log.info("📩 요청 수신: user_id={}, signal-type={}", userId, signalType);
//
//        List<UserStock> stocks = myStockService.getMyStocks(userId, signalType);
//        return ResponseEntity.ok(stocks);
//    }
//}
//


package com.donttouch.chart_similarity_service.domain.my_stock.controller;

import com.donttouch.chart_similarity_service.domain.my_stock.entity.UserStock;
import com.donttouch.chart_similarity_service.domain.my_stock.service.MyStockService;
import com.donttouch.chart_similarity_service.domain.my_stock.service.SignalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/chart-similarity")
@RequiredArgsConstructor
public class MyStockController {

    private final MyStockService myStockService;
    private final SignalService signalService; // ✅ 추가

    /**
     * 내 보유 종목 / 시그널 조회 API
     * @param userId 사용자 UUID
     * @param signalType "buy" 또는 "sell" (선택)
     */
    @GetMapping("/my-stock")
    public ResponseEntity<?> getMyStocks(
            @RequestParam("user_id") String userId,
            @RequestParam(name = "signal-type", required = false) String signalType
    ) {
        log.info("📩 요청 수신: user_id={}, signal-type={}", userId, signalType);

        if (signalType == null || signalType.isBlank()) {
            // 기본 보유 종목 조회
            List<UserStock> stocks = myStockService.getMyStocks(userId);
            return ResponseEntity.ok(stocks);
        } else {
            // signal-type 파라미터 존재 시 → SignalService로 분기
            var signals = signalService.getSignalWithStockInfo(signalType, userId);
            return ResponseEntity.ok(signals);
        }
    }
}
