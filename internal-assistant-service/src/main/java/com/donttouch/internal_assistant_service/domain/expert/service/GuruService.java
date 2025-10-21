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
import com.donttouch.internal_assistant_service.domain.member.repository.DailyStockChartsRepository;
import com.donttouch.internal_assistant_service.domain.member.repository.UserTradesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.donttouch.common_service.auth.entity.User;
import com.donttouch.common_service.auth.entity.vo.MyInfoResponse;
import com.donttouch.common_service.auth.repository.UserRepository;
import com.donttouch.common_service.global.aop.dto.CurrentMemberIdRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
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
                        ((Number) row.get("sellVolume")).doubleValue(),
                        0.0
                ))
                .collect(Collectors.toList());

        double cumulative = 0.0;
        List<GuruTradeData> cumulativeStats = new ArrayList<>();

        for (GuruTradeData data : tradeStats) {
            cumulative += (data.getBuyVolume() - data.getSellVolume());
            cumulativeStats.add(new GuruTradeData(
                    data.getDate(),
                    data.getBuyVolume(),
                    data.getSellVolume(),
                    cumulative
            ));
        }

        return GuruTradeResponse.builder()
                        .stockName(stock.getStockName())
                        .symbol(stock.getSymbol())
                        .period(type)
                        .data(cumulativeStats)
                        .build();
    }


    @Transactional(readOnly = true)
    public GuruVolumeResponse getGuruVolumeRank(Side side, InvestmentType investmentType) {
        List<StockVolumeBatch> stockVolumes = stockVolumeBatchRepository.findBySideAndInvest(side, investmentType);
        System.out.println(stockVolumes.size());
        List<String> stockIds = stockVolumes.stream()
                .map(StockVolumeBatch::getStockId)
                .toList();
        System.out.println(stockIds.size());
        List<Stock> stocks = stockRepository.findAllById(stockIds);
        for (Stock stock : stocks) {
            System.out.println(stock.toString());
        }

        return buildGuruVolumeResponse(stocks);
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
