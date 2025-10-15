package com.donttouch.chart_similarity_service.domain.stock.service;

import com.donttouch.chart_similarity_service.domain.my_stock.entity.SignalBuy;
import com.donttouch.chart_similarity_service.domain.my_stock.entity.SignalSell;
import com.donttouch.chart_similarity_service.domain.my_stock.repository.SignalBuyRepository;
import com.donttouch.chart_similarity_service.domain.my_stock.repository.SignalSellRepository;
import com.donttouch.chart_similarity_service.domain.stock.dto.StockSignalRes;
import com.donttouch.chart_similarity_service.domain.stock.repository.SignalExplainRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final SignalBuyRepository signalBuyRepository;
    private final SignalSellRepository signalSellRepository;
    private final SignalExplainRepository signalExplainRepository;

    public StockSignalRes getSignalInfo(String stockCode, String signalType) {
        log.info("📩 종목별 시그널 조회 요청: code={}, type={}", stockCode, signalType);

        if (signalType.equalsIgnoreCase("buy")) {
            var signal = signalBuyRepository.findTopByStockCodeOrderByCreatedAtDesc(stockCode)
                    .orElseThrow(() -> new RuntimeException("매수 시그널 없음"));

            var explain = signalExplainRepository.findById(signal.getSignalId())
                    .orElseThrow(() -> new RuntimeException("설명 데이터 없음"));

            return StockSignalRes.builder()
                    .stockName(signal.getStockName())
                    .trendPastScaled(signal.getTrendPastScaled())
                    .trendToday(signal.getTrendToday())
                    .todayDate(signal.getTodayDate().toString())
                    .pastDate(signal.getPastDate().toString())
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
                    .trendPastScaled(signal.getTrendPastScaled())
                    .trendToday(signal.getTrendToday())
                    .todayDate(signal.getTodayDate().toString())
                    .pastDate(signal.getPastDate().toString())
                    .description(explain.getDescription())
                    .descriptionDetail(explain.getDescriptionDetail())
                    .build();

        } else {
            throw new IllegalArgumentException("signal-type은 buy 또는 sell만 허용됩니다.");
        }
    }
}
