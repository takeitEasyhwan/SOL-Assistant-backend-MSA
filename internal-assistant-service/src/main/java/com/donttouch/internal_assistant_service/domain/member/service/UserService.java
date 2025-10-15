package com.donttouch.internal_assistant_service.domain.member.service;

import com.donttouch.common_service.stock.entity.DailyStockCharts;
import com.donttouch.common_service.stock.entity.Stock;
import com.donttouch.common_service.stock.entity.UserStocks;
import com.donttouch.common_service.stock.repository.UserStocksRepository;
import com.donttouch.internal_assistant_service.domain.member.entity.UserAssets;
import com.donttouch.internal_assistant_service.domain.member.entity.UserTrades;
import com.donttouch.internal_assistant_service.domain.member.entity.vo.MyStockResponse;
import com.donttouch.internal_assistant_service.domain.member.entity.vo.Side;
import com.donttouch.internal_assistant_service.domain.member.entity.vo.TradeMoneyResponse;
import com.donttouch.internal_assistant_service.domain.member.entity.vo.TradeProfitResponse;
import com.donttouch.internal_assistant_service.domain.member.exception.AssetNotFoundException;
import com.donttouch.internal_assistant_service.domain.member.exception.ChartDataNotFoundException;
import com.donttouch.internal_assistant_service.domain.member.exception.ErrorMessage;
import com.donttouch.internal_assistant_service.domain.member.exception.StockNotFoundException;
import com.donttouch.internal_assistant_service.domain.member.repository.DailyStockChartsRepository;
import com.donttouch.internal_assistant_service.domain.member.repository.UserAssetsRepository;
import com.donttouch.internal_assistant_service.domain.member.repository.UserTradesRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStocksRepository userStocksRepository;
    private final DailyStockChartsRepository dailyStockChartsRepository;
    private final UserAssetsRepository userAssetsRepository;
    private final UserTradesRepository userTradesRepository;

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

    public TradeMoneyResponse getTradeMoney(String userId) {
        UserAssets userAssets = userAssetsRepository.findByUserId(userId)
                .orElseThrow(() -> new AssetNotFoundException(ErrorMessage.STOCK_NOT_FOUND));

        return TradeMoneyResponse.builder()
                .principal(userAssets.getPrincipal())
                .totalBalance(userAssets.getTotalBalance())
                .build();
    }

    @Transactional(readOnly = true)
    public TradeProfitResponse getTradeProfit(String userId, LocalDate startDate, LocalDate endDate) {
        List<UserTrades> trades = userTradesRepository.findByUserIdAndTradeTsBetween(
                userId,
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay()
        );
        List<UserTrades> buyTrades = trades.stream()
                .filter(t -> t.getSide().equals(Side.BUY))
                .toList();

        List<UserTrades> sellTrades = trades.stream()
                .filter(t -> t.getSide().equals(Side.SELL))
                .toList();

        double buyAmount = buyTrades.stream()
                .mapToDouble(t -> t.getPrice() * t.getQuantity())
                .sum();

        double sellAmount = sellTrades.stream()
                .mapToDouble(t -> t.getPrice() * t.getQuantity())
                .sum();


        double realizedProfit = sellAmount - buyAmount;
        double realizedPercent = buyAmount == 0 ? 0 : (realizedProfit / buyAmount) * 100.0;
        List<TradeProfitResponse.TradeDetail> buyList = buyTrades.stream()
                .map(t -> TradeProfitResponse.TradeDetail.builder()
                        .tradeDate(t.getTradeTs())
                        .stockId(t.getStock().getId())
                        .stockName(t.getStock().getStockName())
                        .symbol(t.getStock().getSymbol())
                        .price(t.getPrice())
                        .quantity(t.getQuantity())
                        .totalPrice(t.getPrice() * t.getQuantity())
                        .build())
                .collect(Collectors.toList());

        List<TradeProfitResponse.TradeDetail> sellList = sellTrades.stream()
                .map(t -> TradeProfitResponse.TradeDetail.builder()
                        .tradeDate(t.getTradeTs())
                        .stockId(t.getStock().getId())
                        .stockName(t.getStock().getStockName())
                        .symbol(t.getStock().getSymbol())
                        .price(t.getPrice())
                        .quantity(t.getQuantity())
                        .totalPrice(t.getPrice() * t.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return TradeProfitResponse.builder()
                .realizedProfit(realizedProfit)
                .realizedPercent(Math.round(realizedPercent * 10) / 10.0) // 소수점 1자리
                .sellCount(sellTrades.size())
                .sellAmount(sellAmount)
                .buyCount(buyTrades.size())
                .buyAmount(buyAmount)
                .sellList(sellList)
                .buyList(buyList)
                .build();
    }
}
