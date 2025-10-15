package com.donttouch.internal_assistant_service.domain.member.service;

import com.donttouch.common_service.auth.entity.vo.InvestmentType;
import com.donttouch.common_service.stock.entity.Stock;
import com.donttouch.common_service.stock.repository.StockRepository;
import com.donttouch.common_service.stock.repository.UserStocksRepository;
import com.donttouch.internal_assistant_service.domain.member.entity.GuruTradeData;
import com.donttouch.internal_assistant_service.domain.member.entity.vo.GuruTradeResponse;
import com.donttouch.internal_assistant_service.domain.member.exception.ErrorMessage;
import com.donttouch.internal_assistant_service.domain.member.exception.StockNotFoundException;
import com.donttouch.internal_assistant_service.domain.member.repository.GuruDayTradeRepository;
import com.donttouch.internal_assistant_service.domain.member.repository.UserTradesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuruTradeService   {
    private final GuruDayTradeRepository guruDayTradeRepository;
    private final StockRepository stockRepository;
    private final UserTradesRepository userTradesRepository;
    private final UserStocksRepository userStocksRepository;
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
        Double totalHolding = userStocksRepository.sumTotalHoldings(guruUserIds, stock.getId());

        return GuruTradeResponse.of(stock, type, tradeStats, totalHolding);
    }
}
