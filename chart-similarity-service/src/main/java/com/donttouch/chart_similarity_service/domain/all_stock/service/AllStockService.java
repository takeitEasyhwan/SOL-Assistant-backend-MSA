package com.donttouch.chart_similarity_service.domain.all_stock.service;

// ⬆ AllStockService.java 맨 위에 추가
import com.donttouch.chart_similarity_service.domain.my_stock.entity.SignalBuy;
import com.donttouch.chart_similarity_service.domain.my_stock.entity.SignalSell;
import com.donttouch.chart_similarity_service.domain.my_stock.entity.SignalBuy;
import com.donttouch.chart_similarity_service.domain.my_stock.entity.SignalSell;
import com.donttouch.chart_similarity_service.domain.my_stock.entity.SignalSell;
import com.donttouch.chart_similarity_service.domain.my_stock.repository.SignalBuyRepository;
import com.donttouch.chart_similarity_service.domain.my_stock.repository.SignalSellRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AllStockService {

    private final SignalBuyRepository signalBuyRepository;
    private final SignalSellRepository signalSellRepository;

    /**
     * ✅ 전체 시그널 조회 (매수 or 매도)
     * signal_buy / signal_sell 테이블 내용을 그대로 반환
     */
    public List<Map<String, Object>> getAllSignalStocks(String signalType) {
        log.info("📩 [AllStockService] 전체 시그널 조회 요청: type={}", signalType);

        if ("buy".equalsIgnoreCase(signalType)) {
            return convertEntitiesToMap(signalBuyRepository.findAll(), "BUY");
        }
        else if ("sell".equalsIgnoreCase(signalType)) {
            return convertEntitiesToMap(signalSellRepository.findAll(), "SELL");
        }
        else {
            log.warn("⚠️ 잘못된 signal-type 값: {}", signalType);
            return Collections.emptyList();
        }
    }

    /**
     * 🧩 공통 변환 로직
     * Entity 필드를 그대로 Map으로 변환 (DB 컬럼명 그대로 유지)
     */
    private List<Map<String, Object>> convertEntitiesToMap(List<?> entities, String signalType) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object obj : entities) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("signalType", signalType);

            if (obj instanceof SignalBuy sb) {
                map.put("name", sb.getStockName());
                map.put("stock_code", sb.getStockCode());
                map.put("currentPrice", sb.getTodayClose());
                map.put("change_rate", sb.getChangeRate());
                map.put("today_volume", sb.getTodayVolume());

//                map.put("buy_id", sb.getBuyId());
//                map.put("signal_id", sb.getSignalId());
//                map.put("today_date", sb.getTodayDate());
//                map.put("past_date", sb.getPastDate());
//                map.put("trend_today", sb.getTrendToday());
//                map.put("trend_past_scaled", sb.getTrendPastScaled());
//                map.put("created_at", sb.getCreatedAt());

            }

            if (obj instanceof SignalSell ss) {

                map.put("name", ss.getStockName());
                map.put("stock_code", ss.getStockCode());
                map.put("currentPrice", ss.getTodayClose());
                map.put("change_rate", ss.getChangeRate());
                map.put("today_volume", ss.getTodayVolume());

//                map.put("sell_id", ss.getSellId());
//                map.put("signal_id", ss.getSignalId());
//                map.put("stock_code", ss.getStockCode());
//                map.put("name", ss.getStockName());
//                map.put("today_date", ss.getTodayDate());
//                map.put("past_date", ss.getPastDate());
//                map.put("today_close", ss.getTodayClose());
//                map.put("change_rate", ss.getChangeRate());
//                map.put("trend_today", ss.getTrendToday());
//                map.put("trend_past_scaled", ss.getTrendPastScaled());
//                map.put("created_at", ss.getCreatedAt());
            }

            result.add(map);
        }

        log.info("✅ {} 시그널 {}건 반환 완료", signalType, result.size());
        return result;
    }
}
