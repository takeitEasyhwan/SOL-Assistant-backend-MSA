package com.donttouch.internal_assistant_service.domain.member.service;

import com.donttouch.common_service.stock.entity.DailyStockCharts;
import com.donttouch.common_service.stock.entity.Stock;
import com.donttouch.common_service.stock.entity.UserStocks;
import com.donttouch.common_service.stock.repository.UserStocksRepository;
import com.donttouch.internal_assistant_service.domain.member.entity.UserAssets;
import com.donttouch.internal_assistant_service.domain.member.entity.UserTrades;
import com.donttouch.internal_assistant_service.domain.member.entity.vo.*;
import com.donttouch.internal_assistant_service.domain.member.entity.Side;
import com.donttouch.internal_assistant_service.domain.member.exception.AssetNotFoundException;
import com.donttouch.internal_assistant_service.domain.member.exception.ChartDataNotFoundException;
import com.donttouch.internal_assistant_service.domain.member.exception.ErrorMessage;
import com.donttouch.internal_assistant_service.domain.member.repository.DailyStockChartsRepository;
import com.donttouch.internal_assistant_service.domain.member.repository.UserAssetsRepository;
import com.donttouch.internal_assistant_service.domain.member.repository.UserTradesRepository;
import jakarta.validation.constraints.Null;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public TradeProfitResponse getTradeSimpleProfit(String userId, LocalDate startDate) {
        List<UserTrades> trades = getMonthlyTrades(userId, startDate);

        TradeSummary summary = calculateTradeSummary(trades);
        List<TradeProfitResponse.TradeDetail> allTrades = toTradeDetailList(trades);

        List<TradeProfitResponse.TradeDetail> currentTrades = allTrades.stream().limit(2).toList();

        return buildTradeResponse(summary, currentTrades);
    }

    @Transactional(readOnly = true)
    public TradeProfitResponse getTradeProfit(String userId, LocalDate startDate) {
        List<UserTrades> trades = getMonthlyTrades(userId, startDate);
        TradeSummary summary = calculateTradeSummary(trades);
        List<TradeProfitResponse.TradeDetail> allTrades = toTradeDetailList(trades);

        return buildTradeResponse(summary, allTrades);
    }

    @Transactional(readOnly = true)
    public TradeProfitResponse getTradeProfitSep(String userId, LocalDate startDate) {
        List<UserTrades> trades = getMonthlyTrades(userId, startDate);

        List<UserTrades> buyTrades = new ArrayList<>();
        List<UserTrades> sellTrades = new ArrayList<>();
        for (UserTrades t : trades) {
            if (t.getSide() == Side.BUY) buyTrades.add(t);
            else if (t.getSide() == Side.SELL) sellTrades.add(t);
        }

        double buyAmount = buyTrades.stream().mapToDouble(t -> t.getPrice() * t.getQuantity()).sum();
        double sellAmount = sellTrades.stream().mapToDouble(t -> t.getPrice() * t.getQuantity()).sum();
        double realizedProfit = sellAmount - buyAmount;
        double realizedPercent = buyAmount == 0 ? 0 : (realizedProfit / buyAmount) * 100.0;

        List<TradeProfitResponse.TradeDetail> buyList = toTradeDetailList(buyTrades);
        List<TradeProfitResponse.TradeDetail> sellList = toTradeDetailList(sellTrades);

        return TradeProfitResponse.builder()
                .realizedProfit(realizedProfit)
                .realizedPercent(Math.round(realizedPercent * 10) / 10.0)
                .sellCount(sellTrades.size())
                .sellAmount(sellAmount)
                .buyCount(buyTrades.size())
                .buyAmount(buyAmount)
                .sellList(sellList)
                .buyList(buyList)
                .build();
    }

    private List<UserTrades> getMonthlyTrades(String userId, LocalDate startDate) {
        List<UserTrades> trades = userTradesRepository.findByUserIdAndTradeTsBetween(
                userId,
                startDate.atStartOfDay(),
                startDate.withDayOfMonth(1).plusMonths(1).atStartOfDay()
        );
        trades.sort((a, b) -> b.getTradeTs().compareTo(a.getTradeTs()));
        return trades;
    }

    private TradeSummary calculateTradeSummary(List<UserTrades> trades) {
        double totalBuy = 0, totalSell = 0;
        int buyCount = 0, sellCount = 0;

        for (UserTrades t : trades) {
            double total = t.getPrice() * t.getQuantity();
            if (t.getSide() == Side.BUY) {
                totalBuy += total;
                buyCount++;
            } else if (t.getSide() == Side.SELL) {
                totalSell += total;
                sellCount++;
            }
        }

        double profit = totalSell - totalBuy;
        double percent = totalBuy == 0 ? 0 : (profit / totalBuy) * 100.0;

        return new TradeSummary(totalBuy, totalSell, buyCount, sellCount, profit, percent);
    }

    private List<TradeProfitResponse.TradeDetail> toTradeDetailList(List<UserTrades> trades) {
        List<TradeProfitResponse.TradeDetail> list = new ArrayList<>();
        for (UserTrades t : trades) {
            double totalPrice = t.getPrice() * t.getQuantity();
            list.add(
                    TradeProfitResponse.TradeDetail.builder()
                            .tradeDate(t.getTradeTs())
                            .stockId(t.getStock().getId())
                            .stockName(t.getStock().getStockName())
                            .symbol(t.getStock().getSymbol())
                            .side(t.getSide())
                            .price(t.getPrice())
                            .quantity(t.getQuantity())
                            .totalPrice(totalPrice)
                            .build()
            );
        }
        return list;
    }
    private TradeProfitResponse buildTradeResponse(TradeSummary s, List<TradeProfitResponse.TradeDetail> list) {
        return TradeProfitResponse.builder()
                .realizedProfit(Math.round(s.getRealizedProfit()))
                .realizedPercent(Math.round(s.getRealizedPercent() * 10) / 10.0)
                .sellCount(s.getSellCount())
                .sellAmount(s.getTotalSellAmount())
                .buyCount(s.getBuyCount())
                .buyAmount(s.getTotalBuyAmount())
                .tradeList(list)
                .build();
    }

    @Transactional(readOnly = true)
    public TradeSectorResponse getTradeSector(String userId) {
        List<UserStocks> userStocks = userStocksRepository.findByUserId(userId);
        if (userStocks.isEmpty()) {
            return TradeSectorResponse.builder()
                    .sectorList(List.of())
                    .build();
        }

        double totalValue = 0;
        Map<String, Double> sectorValueMap = new HashMap<>();
        Map<String, String> sectorNameMap = new HashMap<>();

        for (UserStocks us : userStocks) {
            Stock stock = us.getStock();

            DailyStockCharts latest = dailyStockChartsRepository
                    .findTopByStockOrderByCurrentDayDesc(stock)
                    .orElseThrow(() -> new ChartDataNotFoundException(ErrorMessage.CHART_DATA_NOT_FOUND));

            double currentPrice = latest.getClosePrice();
            double value = currentPrice * us.getQuantity();

            String sectorId = stock.getSector().getId();
            String sectorName = stock.getSector().getSectorName();

            sectorValueMap.merge(sectorId, value, Double::sum);
            sectorNameMap.putIfAbsent(sectorId, sectorName);

            totalValue += value;
        }

        List<TradeSectorResponse.SectorDetail> sectorList = new ArrayList<>();

        for (String sectorId : sectorValueMap.keySet()) {
            double sectorValue = sectorValueMap.get(sectorId);
            double percentage = totalValue == 0 ? 0 : (sectorValue / totalValue) * 100.0;

            sectorList.add(
                    TradeSectorResponse.SectorDetail.builder()
                            .sectorId(sectorId)
                            .sectorName(sectorNameMap.get(sectorId))
                            .percentage(Math.round(percentage * 10) / 10.0)
                            .build()
            );
        }
        sectorList.sort((a, b) -> Double.compare(b.getPercentage(), a.getPercentage()));

        return TradeSectorResponse.builder()
                .sectorList(sectorList)
                .build();
    }
}

