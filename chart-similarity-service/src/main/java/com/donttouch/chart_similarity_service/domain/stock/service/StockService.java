package com.donttouch.chart_similarity_service.domain.stock.service;

import com.donttouch.chart_similarity_service.domain.my_stock.repository.SignalBuyRepository;
import com.donttouch.chart_similarity_service.domain.my_stock.repository.SignalSellRepository;
import com.donttouch.chart_similarity_service.domain.stock.dto.StockSignalRes;
import com.donttouch.chart_similarity_service.domain.stock.repository.SignalExplainRepository;
import com.donttouch.common_service.stock.entity.Stock;
import com.donttouch.common_service.stock.entity.UserStocks;
import com.donttouch.common_service.stock.repository.StockRepository;
import com.donttouch.common_service.stock.repository.UserStocksRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Collections;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final SignalBuyRepository signalBuyRepository;
    private final SignalSellRepository signalSellRepository;
    private final SignalExplainRepository signalExplainRepository;
    private final StockRepository stockRepository;
    private final UserStocksRepository userStocksRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // ✅ JSON 파서

    public StockSignalRes getSignalInfo(String stockCode, String signalType, String userId) {
        log.info("📩 종목별 시그널 조회 요청: code={}, type={}", stockCode, signalType);

        if(stockRepository.findBySymbol(stockCode).isEmpty() || !userStocksRepository.existsByUserIdAndStock_Symbol(userId, stockCode))
            return StockSignalRes.builder().build();

        if (signalType.equalsIgnoreCase("buy")) {

            var signal = signalBuyRepository.findTopByStockCodeOrderByCreatedAtDesc(stockCode)
                    .orElseThrow(() -> new RuntimeException("매수 시그널 없음"));

            var explain = signalExplainRepository.findById(signal.getSignalId())
                    .orElseThrow(() -> new RuntimeException("설명 데이터 없음"));

            return StockSignalRes.builder()
                    .stockName(signal.getStockName())
                    .trendPastScaled(parseJsonArray(signal.getTrendPastScaled()))
                    .trendToday(parseJsonArray(signal.getTrendToday()))
                    .todayDate(signal.getTodayDate().toString())
                    .pastDate(signal.getPastDate().toString())
                    .signalType(explain.getSignalType())
                    .description(explain.getDescription())
                    .descriptionDetail(explain.getDescriptionDetail())
                    .build();

        } else if (signalType.equalsIgnoreCase("sell")) {
            var signal = signalSellRepository.findTopByStockCodeOrderByCreatedAtDesc(stockCode)
                    .orElseThrow(() -> new RuntimeException("매도 시그널 없음"));

            var explain = signalExplainRepository.findById(signal.getSignalId())
                    .orElseThrow(() -> new RuntimeException("설명 데이터 없음"));

            return StockSignalRes.builder()
                    .stockName(signal.getStockName())
                    .trendPastScaled(parseJsonArray(signal.getTrendPastScaled()))
                    .trendToday(parseJsonArray(signal.getTrendToday()))
                    .todayDate(signal.getTodayDate().toString())
                    .pastDate(signal.getPastDate().toString())
                    .signalType(explain.getSignalType())
                    .description(explain.getDescription())
                    .descriptionDetail(explain.getDescriptionDetail())
                    .build();

        } else {
            throw new IllegalArgumentException("signal-type은 buy 또는 sell만 허용됩니다.");
        }
    }

    // ✅ JSON 문자열을 List<Map<String, Object>>로 변환
    private List<Map<String, Object>> parseJsonArray(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            log.error("❌ JSON 파싱 실패: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
