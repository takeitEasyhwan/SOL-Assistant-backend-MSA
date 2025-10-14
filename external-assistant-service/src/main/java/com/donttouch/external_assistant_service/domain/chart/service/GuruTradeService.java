package com.donttouch.external_assistant_service.domain.chart.service;

import com.donttouch.common_service.auth.entity.vo.InvestmentType;
import com.donttouch.common_service.stock.entity.Stock;
import com.donttouch.common_service.stock.repository.StockRepository;
import com.donttouch.external_assistant_service.domain.chart.entity.GuruTradeData;
import com.donttouch.external_assistant_service.domain.chart.entity.vo.GuruTradeResponse;
import com.donttouch.external_assistant_service.domain.chart.exception.ErrorMessage;
import com.donttouch.external_assistant_service.domain.chart.exception.StockNotFoundException;
import com.donttouch.external_assistant_service.domain.chart.repository.GuruDayTradeRepository;
import com.donttouch.external_assistant_service.domain.chart.repository.UserTradesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuruTradeService   {
    private final GuruDayTradeRepository guruDayTradeRepository;
    private final StockRepository stockRepository;
    private final UserTradesRepository userTradesRepository;
    private final GuruDayTradeRepository guruSwingRepository;
    private final GuruDayTradeRepository guruHoldRepository;

    public GuruTradeResponse getGuruTrade(String symbol, InvestmentType type) {
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new StockNotFoundException(ErrorMessage.STOCK_NOT_FOUND));

        List<String> guruUserIds = switch (type) {
            case DAY -> guruDayTradeRepository.findAllUserIds();
            case SWING -> guruSwingRepository.findAllUserIds();
            case HOLD -> guruHoldRepository.findAllUserIds();
        };
        List<GuruTradeData> tradeStats = userTradesRepository.aggregateDailyTradeStats(guruUserIds, stock.getId());
        Double totalHolding = userTradesRepository.sumTotalHoldings(guruUserIds, stock.getId());

        return GuruTradeResponse.of(stock, type, tradeStats, totalHolding);
    }
}
