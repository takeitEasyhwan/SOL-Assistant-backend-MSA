package com.donttouch.internal_assistant_service.domain.member.service;

import com.donttouch.common_service.auth.entity.User;
import com.donttouch.common_service.auth.repository.UserRepository;
import com.donttouch.common_service.stock.entity.DailyStockCharts;
import com.donttouch.common_service.stock.entity.Stock;
import com.donttouch.common_service.stock.repository.StockRepository;
import com.donttouch.common_service.stock.entity.UserStocks;
import com.donttouch.internal_assistant_service.domain.member.entity.UserTrades;
import com.donttouch.internal_assistant_service.domain.member.entity.vo.MyStockResponse;
import com.donttouch.internal_assistant_service.domain.member.entity.vo.TradeRequest;
import com.donttouch.internal_assistant_service.domain.member.entity.vo.TradeResponse;
import com.donttouch.internal_assistant_service.domain.member.exception.ChartDataNotFoundException;
import com.donttouch.internal_assistant_service.domain.member.exception.ErrorMessage;
import com.donttouch.internal_assistant_service.domain.member.exception.StockNotFoundException;
import com.donttouch.internal_assistant_service.domain.member.exception.UserNotFoundException;
import com.donttouch.internal_assistant_service.domain.member.repository.DailyStockChartsRepository;
import com.donttouch.common_service.stock.repository.UserStocksRepository;
import com.donttouch.internal_assistant_service.domain.member.repository.UserTradesRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserTradesService {
    private final UserTradesRepository userTradesRepository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;
    private final UserStocksRepository userStocksRepository;
    private final DailyStockChartsRepository dailyStockChartsRepository;

    @Transactional
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

        UserStocks userStock = userStocksRepository.findByUserAndStock(user, stock).orElse(null);

        if (userStock == null) {
            UserStocks newStock = new UserStocks();
            newStock.setUserStockId(UUID.randomUUID().toString());
            newStock.setUser(user);
            newStock.setStock(stock);
            newStock.setQuantity(request.getQuantity());
            newStock.setCostBasis(request.getPrice());
            userStocksRepository.save(newStock);
        } else {
            double oldQty = userStock.getQuantity();
            double oldAvg = userStock.getCostBasis();
            double buyQty = request.getQuantity();
            double buyPrice = request.getPrice();

            double newQty = oldQty + buyQty;
            double newAvg = ((oldQty * oldAvg) + (buyQty * buyPrice)) / newQty;

            userStock.setQuantity(newQty);
            userStock.setCostBasis(newAvg);

            userStocksRepository.save(userStock);
        }

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

    public List<MyStockResponse> getMyStocks(String userId) {
        List<UserStocks> userStocks = userStocksRepository.findByUserId(userId);

        return userStocks.stream()
                .map(us -> {
                    Stock stock = us.getStock();

                    List<DailyStockCharts> chartList =
                            dailyStockChartsRepository.findByStockOrderByCurrentDayDesc(stock);

                    if (chartList.size() < 2) {
                        throw new ChartDataNotFoundException(ErrorMessage.CHART_DATA_NOT_FOUND);
                    }

                    Double currentPrice = chartList.get(0).getClosePrice();
                    Double previousClose = chartList.get(1).getClosePrice();
                    Double changeRate = ((currentPrice - previousClose) / previousClose) * 100;

                    Double diff = currentPrice - us.getCostBasis();
                    Double profit = (diff / us.getCostBasis()) * 100;

                    return MyStockResponse.builder()
                            .stockName(stock.getStockName())
                            .symbol(stock.getSymbol())
                            .currentPrice(currentPrice)
                            .changeRate(Math.round(changeRate * 100) / 100.0)
                            .market(stock.getMarket())
                            .quantity(us.getQuantity())
                            .costBasis(us.getCostBasis())
                            .diff(diff)
                            .profit(Math.round(profit * 100) / 100.0)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
