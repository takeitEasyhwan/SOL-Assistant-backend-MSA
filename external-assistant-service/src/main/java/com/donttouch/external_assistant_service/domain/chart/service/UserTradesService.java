package com.donttouch.external_assistant_service.domain.chart.service;

import com.donttouch.common_service.auth.entity.User;
import com.donttouch.common_service.auth.repository.UserRepository;
import com.donttouch.common_service.stock.entity.Stock;
import com.donttouch.common_service.stock.repository.StockRepository;
import com.donttouch.external_assistant_service.domain.chart.entity.Side;
import com.donttouch.external_assistant_service.domain.chart.entity.UserTrades;
import com.donttouch.external_assistant_service.domain.chart.entity.vo.TradeRequest;
import com.donttouch.external_assistant_service.domain.chart.entity.vo.TradeResponse;
import com.donttouch.external_assistant_service.domain.chart.exception.ErrorMessage;
import com.donttouch.external_assistant_service.domain.chart.exception.StockNotFoundException;
import com.donttouch.external_assistant_service.domain.chart.exception.UserNotFoundException;
import com.donttouch.external_assistant_service.domain.chart.repository.UserTradesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserTradesService {
    private final UserTradesRepository userTradesRepository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;

    public TradeResponse buy(TradeRequest request) {
        Stock stock = stockRepository.findBySymbol(request.getSymbol())
                .orElseThrow(() -> new StockNotFoundException(ErrorMessage.STOCK_NOT_FOUND));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException(ErrorMessage.USER_NOT_FOUND));

        String tradeId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        UserTrades trade = UserTrades.builder()
                .userTradeId(tradeId)
                .user(user)
                .stock(stock)
                .tradeTs(now)
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .side(request.getSide())
                .build();

        userTradesRepository.save(trade);

        return TradeResponse.builder()
                .userTradeId(trade.getUserTradeId())
                .userId(user.getId())
                .stockId(stock.getId())
                .tradeTs(trade.getTradeTs())
                .quantity(trade.getQuantity())
                .price(trade.getPrice())
                .side(trade.getSide())
                .status("SUCCESS")
                .build();
    }

    public TradeResponse sell(TradeRequest request) {
        Stock stock = stockRepository.findBySymbol(request.getSymbol())
                .orElseThrow(() -> new StockNotFoundException(ErrorMessage.STOCK_NOT_FOUND));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException(ErrorMessage.USER_NOT_FOUND));

        String tradeId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        UserTrades trade = UserTrades.builder()
                .userTradeId(tradeId)
                .user(user)
                .stock(stock)
                .tradeTs(now)
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .side(request.getSide())
                .build();

        userTradesRepository.save(trade);

        return TradeResponse.builder()
                .userTradeId(trade.getUserTradeId())
                .userId(user.getId())
                .stockId(stock.getId())
                .tradeTs(trade.getTradeTs())
                .quantity(trade.getQuantity())
                .price(trade.getPrice())
                .side(trade.getSide())
                .status("SUCCESS")
                .build();
    }
}
