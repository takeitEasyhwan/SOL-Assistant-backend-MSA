package com.donttouch.internal_assistant_service.domain.member.service;

import com.donttouch.common_service.auth.entity.User;
import com.donttouch.common_service.auth.repository.UserRepository;
import com.donttouch.common_service.stock.entity.DailyStockCharts;
import com.donttouch.common_service.stock.entity.Stock;
import com.donttouch.common_service.stock.entity.UserStocks;
import com.donttouch.common_service.stock.repository.StockRepository;
import com.donttouch.common_service.stock.repository.UserStocksRepository;
import com.donttouch.internal_assistant_service.domain.exception.*;
import com.donttouch.internal_assistant_service.domain.member.entity.HoldingPeriodDistribution;
import com.donttouch.internal_assistant_service.domain.member.entity.UserAssets;
import com.donttouch.internal_assistant_service.domain.member.entity.UserTrades;
import com.donttouch.internal_assistant_service.domain.member.entity.vo.*;
import com.donttouch.internal_assistant_service.domain.member.entity.Side;
import com.donttouch.internal_assistant_service.domain.member.repository.DailyStockChartsRepository;
import com.donttouch.internal_assistant_service.domain.member.repository.HoldingPeriodDistributionRepository;
import com.donttouch.internal_assistant_service.domain.member.repository.UserAssetsRepository;
import com.donttouch.internal_assistant_service.domain.member.repository.UserTradesRepository;
import jakarta.validation.constraints.Null;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStocksRepository userStocksRepository;
    private final DailyStockChartsRepository dailyStockChartsRepository;
    private final UserAssetsRepository userAssetsRepository;
    private final UserTradesRepository userTradesRepository;
    private final HoldingPeriodDistributionRepository holdingPeriodDistributionRepository;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;

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
                    LocalDateTime currentTradeTs = userTradesRepository.findLatestTradeTimestamp(userId, stock);

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
                            .currentTradeTs(currentTradeTs)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public StockCountResponse getStockCount(String symbol, @Null String userId) {
        List<UserStocks> userStocks = userStocksRepository.findByUserId(userId);
        for(UserStocks s : userStocks) {
            if(s.getStock().getSymbol().equals(symbol)) {
                return StockCountResponse.builder()
                        .quantity(s.getQuantity())
                        .build();
            }
        }
        return StockCountResponse.builder()
                .quantity(0.0)
                .build();
    }

    public TradeTypeResponse getTradeType(String userId) {
        User user = userRepository.findById(userId).
                orElseThrow(()-> new UserNotFoundException(ErrorMessage.USER_NOT_FOUND));

        return TradeTypeResponse.builder()
                .username(user.getName())
                .investmentType(user.getInvestmentType())
                .build();
    }


    public TradeMoneyResponse getTradeMoney(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessage.USER_NOT_FOUND));

        UserAssets userAssets = userAssetsRepository.findByUserId(userId)
                .orElse(null);

        if (userAssets == null) {
            userAssets = UserAssets.builder()
                    .userAssetId(UUID.randomUUID().toString())
                    .user(user)
                    .principal(1_250_300.0)
                    .totalBalance(1_250_300.0)
                    .build();
            userAssetsRepository.save(userAssets);
        }


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

        // 거래일 순 정렬
        trades.sort(Comparator.comparing(UserTrades::getTradeTs));

        double realizedProfit = 0.0;
        int realizedCount = 0;

        // BUY 큐를 FIFO로 사용
        Deque<UserTrades> buyQueue = new ArrayDeque<>();

        for (UserTrades trade : trades) {
            if (trade.getSide() == Side.BUY) {
                buyQueue.addLast(trade);
            } else if (trade.getSide() == Side.SELL) {
                double remaining = trade.getQuantity();

                while (remaining > 0 && !buyQueue.isEmpty()) {
                    UserTrades buy = buyQueue.peekFirst();

                    double matchedQty = Math.min(remaining, buy.getQuantity());
                    double profitPerUnit = trade.getPrice() - buy.getPrice();
                    realizedProfit += profitPerUnit * matchedQty;

                    remaining -= matchedQty;
                    buy.setQuantity(buy.getQuantity() - matchedQty);
                    realizedCount++;

                    if (buy.getQuantity() == 0) {
                        buyQueue.removeFirst();
                    }
                }
            }
        }

        // 실현수익률 계산
        double totalInvested = trades.stream()
                .filter(t -> t.getSide() == Side.SELL)
                .mapToDouble(t -> t.getPrice() * t.getQuantity())
                .sum();

        double realizedPercent = totalInvested == 0 ? 0 : (realizedProfit / totalInvested) * 100.0;

        return TradeProfitResponse.builder()
                .realizedProfit(Math.round(realizedProfit))
                .realizedPercent(Math.round(realizedPercent * 10) / 10.0)
                .tradeList(toTradeDetailList(trades))
                .build();
    }

    public TradeHasMonthResponse getTradeMonths(String userId) {
        List<String> months = userTradesRepository.findDistinctTradeMonths(userId);
        return TradeHasMonthResponse.builder()
                .months(months)
                .totalMonths(months.size())
                .build();
    }

    @Transactional(readOnly = true)
    public TradeProfitResponse getTradeProfitSep(String userId, LocalDate startDate) {
        List<UserTrades> trades = getMonthlyTrades(userId, startDate);

        TradeSummary summary = calculateTradeSummary(trades);
        List<UserTrades> buyTrades = trades.stream().filter(t -> t.getSide() == Side.BUY).toList();
        List<UserTrades> sellTrades = trades.stream().filter(t -> t.getSide() == Side.SELL).toList();

        List<TradeProfitResponse.TradeDetail> buyList = toTradeDetailList(buyTrades);
        List<TradeProfitResponse.TradeDetail> sellList = toTradeDetailList(sellTrades);

        return TradeProfitResponse.builder()
                .realizedProfit(Math.round(summary.getRealizedProfit()))
                .realizedPercent(Math.round(summary.getRealizedPercent() * 10) / 10.0)
                .sellCount(summary.getSellCount())
                .sellAmount(summary.getTotalSellAmount())
                .buyCount(summary.getBuyCount())
                .buyAmount(summary.getTotalBuyAmount())
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
        if (trades == null || trades.isEmpty()) {
            return new TradeSummary(0, 0, 0, 0, 0, 0);
        }

        // 거래일순 정렬 (FIFO)
        trades.sort(Comparator.comparing(UserTrades::getTradeTs));

        Deque<UserTrades> buyQueue = new ArrayDeque<>();

        double totalBuyAmount = 0.0;
        double totalSellAmount = 0.0;
        int buyCount = 0;
        int sellCount = 0;

        double realizedProfit = 0.0;
        double realizedBaseAmount = 0.0; // 실현 손익률의 분모 (매도된 물량의 매입원가 합계)

        for (UserTrades trade : trades) {
            double amount = trade.getPrice() * trade.getQuantity();

            if (trade.getSide() == Side.BUY) {
                // BUY 거래는 큐에 쌓는다 (FIFO)
                buyQueue.addLast(UserTrades.builder()
                        .tradeTs(trade.getTradeTs())
                        .price(trade.getPrice())
                        .quantity(trade.getQuantity())
                        .build());
                totalBuyAmount += amount;
                buyCount++;

            } else if (trade.getSide() == Side.SELL) {
                double remaining = trade.getQuantity();
                totalSellAmount += amount;
                sellCount++;

                // SELL 수량만큼 BUY 큐에서 꺼내서 실현손익 계산
                while (remaining > 0 && !buyQueue.isEmpty()) {
                    UserTrades buy = buyQueue.peekFirst();
                    double matchedQty = Math.min(remaining, buy.getQuantity());

                    double profitPerUnit = trade.getPrice() - buy.getPrice();
                    realizedProfit += profitPerUnit * matchedQty;
                    realizedBaseAmount += buy.getPrice() * matchedQty;

                    // FIFO 소진 처리
                    remaining -= matchedQty;
                    buy.setQuantity(buy.getQuantity() - matchedQty);
                    if (buy.getQuantity() == 0) buyQueue.removeFirst();
                }
            }
        }

        double realizedPercent = realizedBaseAmount == 0 ? 0 : (realizedProfit / realizedBaseAmount) * 100.0;

        return new TradeSummary(
                totalBuyAmount,
                totalSellAmount,
                buyCount,
                sellCount,
                realizedProfit,
                realizedPercent
        );
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

    public HoldingPeriodResponse getTradeRecord(String userId) {
        List<UserTrades> trades = userTradesRepository.findByUserId(userId);
        double avgHoldingDays = calculateAverageHoldingDays(trades);
        List<HoldingPeriodDistribution> distributions = holdingPeriodDistributionRepository.findAllByOrderByQuantileAsc();
        double quantile = 0.0;

        for (HoldingPeriodDistribution d : distributions) {
            if (avgHoldingDays >= d.getHoldingDays()) {
                quantile = d.getQuantile();
            } else {
                break;
            }
        }

        return HoldingPeriodResponse.builder()
                .averageHoldingDays(Math.round(avgHoldingDays * 10.0) / 10.0)
                .quantile(Math.round(quantile * 100.0) / 100.0)
                .build();

    }

    private double calculateAverageHoldingDays(List<UserTrades> trades) {
        if (trades == null || trades.isEmpty()) return 0.0;
        trades.sort(Comparator.comparing(UserTrades::getTradeTs));

        Deque<UserTrades> buyQueue = new ArrayDeque<>();

        double totalDays = 0.0;
        int matchedQuantity = 0;

        for (UserTrades trade : trades) {
            if (trade.getSide() == Side.BUY) {
                buyQueue.addLast(UserTrades.builder()
                        .tradeTs(trade.getTradeTs())
                        .quantity(trade.getQuantity())
                        .build());
            } else if (trade.getSide() == Side.SELL) {
                double remaining = trade.getQuantity();

                while (remaining > 0 && !buyQueue.isEmpty()) {
                    UserTrades buy = buyQueue.peekFirst();
                    int matched = (int)Math.min(remaining, buy.getQuantity());
                    long days = java.time.Duration.between(
                            buy.getTradeTs(), trade.getTradeTs()
                    ).toDays();

                    totalDays += days * matched;
                    matchedQuantity += matched;

                    remaining -= matched;
                    buy.setQuantity(buy.getQuantity() - matched);

                    if (buy.getQuantity() == 0) {
                        buyQueue.removeFirst();
                    }
                }
            }
        }

        return matchedQuantity == 0 ? 0.0 : totalDays / matchedQuantity;
    }
}

