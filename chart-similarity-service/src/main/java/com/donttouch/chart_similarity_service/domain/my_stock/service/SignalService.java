package com.donttouch.chart_similarity_service.domain.my_stock.service;

import com.donttouch.chart_similarity_service.domain.my_stock.entity.SignalBuy;
import com.donttouch.chart_similarity_service.domain.my_stock.entity.SignalSell;
import com.donttouch.chart_similarity_service.domain.my_stock.entity.Stocks;
import com.donttouch.chart_similarity_service.domain.my_stock.repository.SignalBuyRepository;
import com.donttouch.chart_similarity_service.domain.my_stock.repository.SignalSellRepository;
import com.donttouch.chart_similarity_service.domain.my_stock.repository.StocksRepository;
import com.donttouch.chart_similarity_service.domain.my_stock.repository.UserStockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignalService {

    private final SignalBuyRepository signalBuyRepository;
    private final SignalSellRepository signalSellRepository;
    private final StocksRepository stocksRepository;
    private final UserStockRepository userStockRepository;

    /**
     * ✅ 사용자 보유 종목에 해당하는 시그널 + 종목명/심볼 조회
     */
    public List<Map<String, Object>> getSignalWithStockInfo(String signalType, String userId) {
        log.info("📩 [SignalService] 요청 수신: signal-type={}, userId={}", signalType, userId);

        // 1️⃣ 유저 보유 종목(stock_id) 조회
        List<String> ownedStockIds = userStockRepository.findByUserId(userId)
                .stream()
                .map(us -> us.getStockId())
                .filter(Objects::nonNull)
                .toList();

        if (ownedStockIds.isEmpty()) {
            log.warn("⚠️ userId={} 보유 종목 없음", userId);
            return Collections.emptyList();
        }

        // 2️⃣ 보유 종목의 symbol(종목코드) 목록으로 변환
        List<Stocks> ownedStocks = stocksRepository.findAllById(ownedStockIds);
        Set<String> ownedSymbols = ownedStocks.stream()
                .map(Stocks::getSymbol)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        log.info("📊 userId={} 보유 종목 symbol 목록: {}", userId, ownedSymbols);

        // 3️⃣ signal 테이블에서 데이터 로드
        List<?> signalList;
        if ("buy".equalsIgnoreCase(signalType)) {
            signalList = signalBuyRepository.findAll();
        } else if ("sell".equalsIgnoreCase(signalType)) {
            signalList = signalSellRepository.findAll();
        } else {
            log.warn("⚠️ 잘못된 signal-type 값: {}", signalType);
            return List.of();
        }

        if (signalList.isEmpty()) {
            log.info("⚪ signal 데이터 없음");
            return List.of();
        }

        // 4️⃣ stock_code 목록 추출 후 stocks 매핑
        List<String> stockCodes = signalList.stream()
                .map(s -> {
                    if (s instanceof SignalBuy sb) return sb.getStockCode();
                    if (s instanceof SignalSell ss) return ss.getStockCode();
                    return null;
                })
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        List<Stocks> stocks = stocksRepository.findAllBySymbolIn(stockCodes);
        Map<String, Stocks> stockMap = stocks.stream()
                .collect(Collectors.toMap(Stocks::getSymbol, s -> s));

        // 5️⃣ 사용자 보유 종목(symbol 기준)에 해당하는 시그널만 필터링 후 조합
        List<Map<String, Object>> combined = new ArrayList<>();

        for (Object s : signalList) {
            String stockCode = null;
            Map<String, Object> item = new LinkedHashMap<>();

            if (s instanceof SignalBuy sb) {
                stockCode = sb.getStockCode();
                if (!ownedSymbols.contains(stockCode)) continue; // ✅ symbol 기준 비교
                item.put("signalId", sb.getSignalId());
                item.put("stockCode", sb.getStockCode());
                item.put("todayClose", sb.getTodayClose());
                item.put("todayDate", sb.getTodayDate());
            } else if (s instanceof SignalSell ss) {
                stockCode = ss.getStockCode();
                if (!ownedSymbols.contains(stockCode)) continue; // ✅ symbol 기준 비교
                item.put("signalId", ss.getSignalId());
                item.put("stockCode", ss.getStockCode());
                item.put("todayClose", ss.getTodayClose());
                item.put("todayDate", ss.getTodayDate());
            }

            // stocks 테이블에서 symbol → stock_id, stockName 매핑
            Stocks stock = stockMap.get(stockCode);
            if (stock != null) {
                item.put("stockId", stock.getStockId());
                item.put("symbol", stock.getSymbol());
                item.put("stockName", stock.getStockName());
            }

            combined.add(item);
        }

        log.info("✅ userId={} / signal-type={} → 최종 조합 {}건", userId, signalType, combined.size());
        return combined;
    }
}

