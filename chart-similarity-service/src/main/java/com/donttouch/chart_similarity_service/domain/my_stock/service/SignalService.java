package com.donttouch.chart_similarity_service.domain.my_stock.service;

import com.donttouch.chart_similarity_service.domain.my_stock.entity.SignalBuy;
import com.donttouch.chart_similarity_service.domain.my_stock.entity.SignalSell;
import com.donttouch.chart_similarity_service.domain.my_stock.entity.Stocks;
import com.donttouch.chart_similarity_service.domain.my_stock.entity.vo.StockMainSignalResponse;
import com.donttouch.chart_similarity_service.domain.my_stock.repository.SignalBuyRepository;
import com.donttouch.chart_similarity_service.domain.my_stock.repository.SignalSellRepository;
import com.donttouch.chart_similarity_service.domain.my_stock.repository.StocksRepository;
import com.donttouch.chart_similarity_service.domain.my_stock.repository.UserStockRepository;
import jakarta.validation.constraints.Null;
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
    public Optional<List<Map<String, Object>>> getSignalWithStockInfo(String signalType, String userId) {
        log.info("📩 [SignalService] 요청 수신: signal-type={}, userId={}", signalType, userId);

        try {
            // 1️⃣ 유저 보유 종목(stock_id) 조회
            List<String> ownedStockIds = userStockRepository.findByUserId(userId)
                    .stream()
                    .map(us -> us.getStockId())
                    .filter(Objects::nonNull)
                    .toList();

            if (ownedStockIds.isEmpty()) {
                log.warn("⚠️ userId={} 보유 종목 없음", userId);
                return Optional.empty();
            }

            // 2️⃣ 보유 종목의 symbol(종목코드) 목록으로 변환
            List<Stocks> ownedStocks = stocksRepository.findAllById(ownedStockIds);
            Set<String> ownedSymbols = ownedStocks.stream()
                    .map(Stocks::getSymbol)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // 3️⃣ signal 테이블에서 데이터 로드
            List<?> signalList;
            if ("buy".equalsIgnoreCase(signalType)) {
                signalList = signalBuyRepository.findAll();
            } else if ("sell".equalsIgnoreCase(signalType)) {
                signalList = signalSellRepository.findAll();
            } else {
                log.warn("⚠️ 잘못된 signal-type 값: {}", signalType);
                return Optional.empty();
            }

            if (signalList.isEmpty()) {
                log.info("⚪ signal 데이터 없음");
                return Optional.empty();
            }

            // 4️⃣ stocks 테이블 매핑 (symbol → stock)
            List<Stocks> stocks = stocksRepository.findAll();
            Map<String, Stocks> stockMap = stocks.stream()
                    .collect(Collectors.toMap(Stocks::getSymbol, s -> s));

            // 5️⃣ 사용자 보유 종목(symbol 기준)에 해당하는 시그널만 필터링 후 조합
            List<Map<String, Object>> combined = new ArrayList<>();

            for (Object s : signalList) {
                String stockCode = null;
                Map<String, Object> item = new LinkedHashMap<>();

                if (s instanceof SignalBuy sb) {
                    stockCode = sb.getStockCode();
                    if (!ownedSymbols.contains(stockCode)) continue;

                    item.put("signalType", "BUY");
                    item.put("name", sb.getStockName());
                    item.put("stock_code", sb.getStockCode());
                    item.put("currentPrice", sb.getTodayClose());
                    item.put("change_rate", sb.getChangeRate());
                    item.put("today_volume", sb.getTodayVolume());
                }

                else if (s instanceof SignalSell ss) {
                    stockCode = ss.getStockCode();
                    if (!ownedSymbols.contains(stockCode)) continue;

                    item.put("signalType", "SELL");
                    item.put("name", ss.getStockName());
                    item.put("stock_code", ss.getStockCode());
                    item.put("currentPrice", ss.getTodayClose());
                    item.put("change_rate", ss.getChangeRate());
                    item.put("today_volume", ss.getTodayVolume());
                }

                combined.add(item);
            }

            log.info("✅ userId={} / signal-type={} → 최종 조합 {}건", userId, signalType, combined.size());
            return combined.isEmpty() ? Optional.empty() : Optional.of(combined);

        } catch (Exception e) {
            log.error("❌ 시그널 조회 중 오류 발생: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    public StockMainSignalResponse getStockMainSignal(String symbol, String userId) {

        boolean buySignal = signalBuyRepository.existsByStockCode(symbol);
        boolean sellSignal = signalSellRepository.existsByStockCode(symbol);


        return StockMainSignalResponse.builder()
                .symbol(symbol)
                .buySignal(buySignal)
                .sellSignal(sellSignal)
                .build();
    }
}
