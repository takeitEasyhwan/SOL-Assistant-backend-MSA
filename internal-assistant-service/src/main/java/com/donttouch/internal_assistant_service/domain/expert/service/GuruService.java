package com.donttouch.internal_assistant_service.domain.expert.service;

import com.donttouch.common_service.auth.entity.InvestmentType;
import com.donttouch.common_service.stock.entity.DailyStockCharts;
import com.donttouch.common_service.stock.entity.Stock;
import com.donttouch.common_service.stock.repository.StockRepository;
import com.donttouch.common_service.stock.repository.UserStocksRepository;
import com.donttouch.internal_assistant_service.domain.exception.UserNotFoundException;
import com.donttouch.internal_assistant_service.domain.expert.entity.*;
import com.donttouch.internal_assistant_service.domain.expert.entity.batch.StockViewBatch;
import com.donttouch.internal_assistant_service.domain.expert.entity.batch.StockVolumeBatch;
import com.donttouch.internal_assistant_service.domain.expert.entity.vo.*;
import com.donttouch.internal_assistant_service.domain.expert.repository.*;
import com.donttouch.internal_assistant_service.domain.exception.ErrorMessage;
import com.donttouch.internal_assistant_service.domain.exception.StockNotFoundException;
import com.donttouch.internal_assistant_service.domain.member.entity.Side;
import com.donttouch.internal_assistant_service.domain.member.entity.UserTrades;
import com.donttouch.internal_assistant_service.domain.member.repository.DailyStockChartsRepository;
import com.donttouch.internal_assistant_service.domain.member.repository.UserTradesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import com.donttouch.common_service.auth.entity.User;
import com.donttouch.common_service.auth.entity.vo.MyInfoResponse;
import com.donttouch.common_service.auth.repository.UserRepository;
import com.donttouch.common_service.global.aop.dto.CurrentMemberIdRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuruService {

    private final GuruDayRepository guruDayRepository;
    private final StockRepository stockRepository;
    private final UserTradesRepository userTradesRepository;
    private final UserStocksRepository userStocksRepository;
    private final GuruSwingRepository guruSwingRepository;
    private final GuruHoldRepository guruHoldRepository;
    private final DailyStockChartsRepository dailyStockChartsRepository;
    private final StockViewBatchRepository stockViewBatchRepository;
    private final StockVolumeBatchRepository stockVolumeBatchRepository;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final StringRedisTemplate trackingRedisTemplate;
    private final UserRepository userRepository;
    private final UserTrackingRepository userTrackingRepository;

    private static final String KEY_PREFIX = "ut:USER:";

    @Transactional(readOnly = true)
    public GuruTradeResponse getGuruTrade(String symbol, InvestmentType type) {
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new StockNotFoundException(ErrorMessage.STOCK_NOT_FOUND));
        List<String> guruUserIds = switch (type) {
            case DAY -> guruDayRepository.findAllUserIds();
            case SWING -> guruSwingRepository.findAllUserIds();
            case HOLD -> guruHoldRepository.findAllUserIds();
        };

        List<Map<String, Object>> rows = userTradesRepository.aggregateGuruTradeDataByDate(guruUserIds, stock.getId());

        List<GuruTradeData> tradeStats = rows.stream()
                .map(row -> new GuruTradeData(
                        ((java.sql.Date) row.get("tradeDate")).toLocalDate(),
                        ((Number) row.get("buyVolume")).doubleValue(),
                        ((Number) row.get("sellVolume")).doubleValue()
                ))
                .collect(Collectors.toList());

        return GuruTradeResponse.builder()
                        .stockName(stock.getStockName())
                        .symbol(stock.getSymbol())
                        .period(type)
                        .data(tradeStats)
                        .build();
    }


    @Transactional(readOnly = true)
    public GuruVolumeResponse getGuruVolumeRank(Side side, InvestmentType investmentType) {
        List<StockVolumeBatch> stockVolumes = stockVolumeBatchRepository.findBySideAndInvest(side, investmentType);

        List<String> stockIds = stockVolumes.stream()
                .map(StockVolumeBatch::getStockId)
                .toList();

        List<Stock> stocks = stockRepository.findAllById(stockIds);


        List<String> guruUserIds = switch (investmentType) {
            case DAY -> guruDayRepository.findAllUserIds();
            case SWING -> guruSwingRepository.findAllUserIds();
            case HOLD -> guruHoldRepository.findAllUserIds();
        };
        long start = System.currentTimeMillis();

        List<UserTrades> userTrades = userTradesRepository.findLatestTwoDaysByStockIds(stockIds, guruUserIds);

        long end = System.currentTimeMillis();

        System.out.println("쿼리 실행 시간: " + (end - start) + "ms");
        GuruVolumeResponse guruVolumeResponseCurrent = buildGuruVolumeResponse(stocks);

        Map<String, Map<String, Double>> guruVolumeMap = new HashMap<>();
        for (UserTrades trade : userTrades) {
            String symbol = trade.getStock().getSymbol();
            guruVolumeMap.putIfAbsent(symbol, new HashMap<>());
            Map<String, Double> volume = guruVolumeMap.get(symbol);

            double prevBuy = volume.getOrDefault("BUY", 0.0);
            double prevSell = volume.getOrDefault("SELL", 0.0);

            if (trade.getSide() == Side.BUY) {
                volume.put("BUY", prevBuy + trade.getQuantity());
            } else {
                volume.put("SELL", prevSell + trade.getQuantity());
            }
        }

        List<GuruVolumeResponse.GuruStockVolumeDto> updatedList = guruVolumeResponseCurrent.getStockVolumeList().stream()
                .map(dto -> {
                    Map<String, Double> volume = guruVolumeMap.getOrDefault(dto.getStockSymbol(), Map.of("BUY",0.0,"SELL",0.0));
                    double buy = volume.get("BUY");
                    double sell = volume.get("SELL");
                    double total = buy + sell;
                    double percent = total > 0 ? (buy - sell) / total : 0.0;

                    // 기존 dto 값은 그대로, guruVolume 값만 새로 추가
                    return GuruVolumeResponse.GuruStockVolumeDto.builder()
                            .stockSymbol(dto.getStockSymbol())
                            .stockName(dto.getStockName())
                            .yesterdayClosePrice(dto.getYesterdayClosePrice())
                            .todayClosePrice(dto.getTodayClosePrice())
                            .priceChangePercent(dto.getPriceChangePercent())
                            .yesterdayVolume(dto.getYesterdayVolume())
                            .todayVolume(dto.getTodayVolume())
                            .volumeChangePercent(dto.getVolumeChangePercent())
                            .guruBuyVolume(buy)
                            .guruSellVolume(sell)
                            .guruVolumePercent(percent)
                            .build();
                })
                .toList();

// GuruVolumeResponse 새로 생성
        guruVolumeResponseCurrent = GuruVolumeResponse.builder()
                .date(guruVolumeResponseCurrent.getDate())
                .stockVolumeList(updatedList)
                .build();

        return guruVolumeResponseCurrent;
    }

    @Transactional(readOnly = true)
    public GuruVolumeResponse guruView(InvestmentType investmentType) {
        List<StockViewBatch> stockViews = stockViewBatchRepository.findByInvestment(investmentType);
        List<String> stockIds = stockViews.stream()
                .map(StockViewBatch::getStockId)
                .toList();
        List<Stock> stocks = stockRepository.findAllById(stockIds);


        return buildGuruVolumeResponse(stocks);
    }

    @Transactional(readOnly = true)
    public MyInfoResponse getMyInfo(CurrentMemberIdRequest currentMemberIdRequest) {

        User user = userRepository.findById(currentMemberIdRequest.getUserUuid()).
                orElseThrow(()-> new UserNotFoundException(ErrorMessage.USER_NOT_FOUND));

        return MyInfoResponse.builder()
                .id(user.getId())
                .investmentType(user.getInvestmentType())
                .name(user.getName())
                .build();
    }


    @Transactional(readOnly = true)
    public GuruVolumeResponse getMyViewStocksGuru(CurrentMemberIdRequest currentMemberIdRequest) {

        List<UserTracking> userTrackings = userTrackingRepository.findByUserIdIn(
                Collections.singletonList(currentMemberIdRequest.getUserUuid())
        );

        Map<String, BigDecimal> stockScores = userTrackings.stream()
                .collect(Collectors.groupingBy(
                        UserTracking::getStockId,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                ut -> ut.getScore() == null ? BigDecimal.ZERO : BigDecimal.valueOf(ut.getScore()),
                                BigDecimal::add
                        )
                ));

        List<String> topStockIds = stockScores.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();

        List<Stock> topStocks = stockRepository.findAllById(topStockIds);

        Map<String, Stock> stockMap = topStocks.stream()
                .collect(Collectors.toMap(Stock::getId, s -> s));

        List<Stock> sortedStocks = topStockIds.stream()
                .map(stockMap::get)
                .toList();

        return buildGuruVolumeResponse(sortedStocks);
    }


    public GuruVolumeResponse buildGuruVolumeResponse(List<Stock> topStocks) {
        List<String> stockIds = topStocks.stream()
                .map(Stock::getId)
                .toList();

        List<DailyStockCharts> dailyChartsList = dailyStockChartsRepository.findByStockIds(stockIds);

        Map<String, List<DailyStockCharts>> chartsByStockId = dailyChartsList.stream()
                .collect(Collectors.groupingBy(d -> d.getStock().getId()));

        List<GuruVolumeResponse.GuruStockVolumeDto> stockVolumeList = new ArrayList<>();

        for (Stock stock : topStocks) {
            List<DailyStockCharts> charts = chartsByStockId.getOrDefault(stock.getId(), Collections.emptyList());

            List<DailyStockCharts> recentTwo = charts.stream()
                    .sorted((d1, d2) -> d2.getCurrentDay().compareTo(d1.getCurrentDay()))
                    .limit(2)
                    .toList();

            if (recentTwo.size() == 2) {
                DailyStockCharts todayChart = recentTwo.get(0);
                DailyStockCharts yesterdayChart = recentTwo.get(1);

                double priceChangePercent = (todayChart.getClosePrice() - yesterdayChart.getClosePrice())
                        / yesterdayChart.getClosePrice() * 100;

                double volumeChangePercent = (todayChart.getVolume() - yesterdayChart.getVolume())
                        * 100.0 / yesterdayChart.getVolume();

                stockVolumeList.add(GuruVolumeResponse.GuruStockVolumeDto.builder()
                        .stockSymbol(stock.getSymbol())
                        .stockName(stock.getStockName())
                        .yesterdayClosePrice(yesterdayChart.getClosePrice())
                        .todayClosePrice(todayChart.getClosePrice())
                        .priceChangePercent(priceChangePercent)
                        .yesterdayVolume(yesterdayChart.getVolume())
                        .todayVolume(todayChart.getVolume())
                        .volumeChangePercent(volumeChangePercent)
                        .build());
            }
        }

        String latestDate = dailyChartsList.stream()
                .map(DailyStockCharts::getCurrentDay)
                .max(LocalDate::compareTo)
                .map(LocalDate::toString)
                .orElse(LocalDate.now().toString());

        return GuruVolumeResponse.builder()
                .date(latestDate)
                .stockVolumeList(stockVolumeList)
                .build();
    }


    public UserTrackingResponse collectBatch(List<UserTrackingRequest.UserTrackingEvent> events) {

        Map<String, Double> agg = events.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getUserId() + ":" + e.getStockId(),
                        Collectors.summingDouble(UserTrackingRequest.UserTrackingEvent::getDeltaScore)
                ));

        agg.forEach((key, value) -> {
            String redisKey = KEY_PREFIX + key;
            trackingRedisTemplate.opsForValue().increment(redisKey, value);
            trackingRedisTemplate.expire(redisKey, 1, TimeUnit.DAYS);
        });

        return UserTrackingResponse.builder()
                .processedCount(events.size())
                .build();
    }

    public void flushRedisToKafka() {
        trackingRedisTemplate.keys(KEY_PREFIX + "*").forEach(redisKey -> {
            String scoreStr = trackingRedisTemplate.opsForValue().get(redisKey);
            if (scoreStr != null) {
                Double score = Double.valueOf(scoreStr);
                trackingRedisTemplate.delete(redisKey);

                String[] parts = redisKey.replace(KEY_PREFIX, "").split(":");
                String userId = parts[0];
                String stockId = parts[1];

                kafkaTemplate.send("user-tracking-topic", userId + ":" + stockId, Map.of(
                        "userId", userId,
                        "stockId", stockId,
                        "score", score
                ));
            }
        });
    }

}
