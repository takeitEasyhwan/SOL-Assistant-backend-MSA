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
//@RequestMapping("/api/v1/insight/chart-similarity")
//@RequiredArgsConstructor
//public class MyStockController {
//
//    private final MyStockService myStockService;
//
//    /**
//     * 내 보유 종목 조회 API
//     * - 기본적으로 user_stocks에서 조회
//     * - signal-type=buy -> signal_buy 추가 조회
//     * - signal-type=sell -> signal_sell 추가 조회
//     */
//    @GetMapping("/my-stock")
//    public ResponseEntity<List<UserStock>> getMyStocks(
//            @RequestParam("user_id") String userId,
//            @RequestParam(name = "signal-type", required = false) String signalType
//    ) {
//        log.info("📩 요청 수신: user_id={}, signal-type={}", userId, signalType);
//
//        List<UserStock> stocks = myStockService.getMyStocksWithSignal(userId, signalType);
//        return ResponseEntity.ok(stocks);
//    }
//}


package com.donttouch.chart_similarity_service.domain.my_stock.repository;

import com.donttouch.chart_similarity_service.domain.my_stock.entity.UserStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserStockRepository extends JpaRepository<UserStock, String> {
    List<UserStock> findByUserId(String userId);
}
