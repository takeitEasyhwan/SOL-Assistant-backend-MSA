package com.donttouch.chart_similarity_service.domain.my_stock.service;

import com.donttouch.chart_similarity_service.domain.my_stock.entity.UserStock;
import com.donttouch.chart_similarity_service.domain.my_stock.entity.Stocks;
import com.donttouch.chart_similarity_service.domain.my_stock.repository.UserStockRepository;
import com.donttouch.chart_similarity_service.domain.my_stock.repository.StocksRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyStockService {

    private final UserStockRepository userStockRepository;
    private final StocksRepository stocksRepository;

    /**
     * ✅ 기본 보유 종목 조회
     * (signalType은 무시됨 — 컨트롤러 분기용)
     */
    public List<UserStock> getMyStocks(String userId, String signalType) {
        log.info("🔍 DB 조회 시작: userId={}, signalType={}", userId, signalType);
        List<UserStock> stocks = userStockRepository.findByUserId(userId);
        log.info("✅ 조회 결과 {}건", stocks.size());
        return stocks;
    }

    /**
     * ✅ 오버로드: signalType 없이 단순 조회
     * (컨트롤러에서 signal-type 파라미터 없을 때 호출)
     */
    public List<UserStock> getMyStocks(String userId) {
        return getMyStocks(userId, null);
    }

    /**
     * ✅ 확장 로직: user_stocks + stocks 조합
     * (stock_id 기준으로 symbol, stock_name 매핑)
     */
    public List<Map<String, Object>> getMyStocksWithNames(String userId) {
        log.info("📊 [MyStockService] 사용자 보유종목 + 종목명 매핑 시작: {}", userId);

        // 1️⃣ 유저의 보유 종목 가져오기
        List<UserStock> userStocks = userStockRepository.findByUserId(userId);
        if (userStocks.isEmpty()) {
            log.warn("⚠️ 보유 종목 없음: userId={}", userId);
            return Collections.emptyList();
        }

        // 2️⃣ stock_id 목록 추출
        List<String> stockIds = userStocks.stream()
                .map(UserStock::getStockId)
                .collect(Collectors.toList());

        // 3️⃣ stocks 테이블에서 해당 종목 정보 가져오기
        List<Stocks> stockInfos = stocksRepository.findAllById(stockIds);
        Map<String, Stocks> stockMap = stockInfos.stream()
                .collect(Collectors.toMap(Stocks::getStockId, s -> s));

        log.info("🧩 stockInfos.size()={}, stockMap.size()={}", stockInfos.size(), stockMap.size());

        // 4️⃣ 결과 조합 (보유 정보 + 종목명 + 코드)
        List<Map<String, Object>> result = new ArrayList<>();
        for (UserStock us : userStocks) {
            Stocks stock = stockMap.get(us.getStockId());
            if (stock == null) continue; // 매칭 안 되면 스킵

            Map<String, Object> item = new HashMap<>();
            item.put("userStocksId", us.getUserStocksId());
            item.put("userId", us.getUserId());
            item.put("stockId", us.getStockId());
            item.put("quantity", us.getQuantity());
            item.put("costBasis", us.getCostBasis());
            item.put("symbol", stock.getSymbol());
            item.put("stockName", stock.getStockName());
            result.add(item);
        }

        log.info("✅ 최종 조합 완료: {}건", result.size());
        return result;
    }
}
