package com.donttouch.internal_assistant_service.domain.expert.service;

import com.donttouch.common_service.auth.entity.InvestmentType;
import com.donttouch.common_service.auth.repository.UserRepository;
import com.donttouch.common_service.stock.entity.DailyStockCharts;
import com.donttouch.common_service.stock.entity.Stock;
import com.donttouch.common_service.stock.repository.StockRepository;
import com.donttouch.common_service.stock.repository.UserStocksRepository;
import com.donttouch.internal_assistant_service.domain.expert.entity.GuruDay;
import com.donttouch.internal_assistant_service.domain.expert.entity.GuruHold;
import com.donttouch.internal_assistant_service.domain.expert.entity.GuruSwing;
import com.donttouch.internal_assistant_service.domain.expert.entity.UserTracking;
import com.donttouch.internal_assistant_service.domain.expert.entity.batch.StockViewBatch;
import com.donttouch.internal_assistant_service.domain.expert.entity.batch.StockVolumeBatch;
import com.donttouch.internal_assistant_service.domain.expert.entity.vo.GuruVolumeResponse;
import com.donttouch.internal_assistant_service.domain.expert.repository.*;
import com.donttouch.internal_assistant_service.domain.member.entity.Side;
import com.donttouch.internal_assistant_service.domain.member.entity.UserTrades;
import com.donttouch.internal_assistant_service.domain.member.repository.DailyStockChartsRepository;
import com.donttouch.internal_assistant_service.domain.member.repository.UserTradesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuruBatchService {

    private final GuruDayRepository guruDayRepository;
    private final GuruSwingRepository guruSwingRepository;
    private final GuruHoldRepository guruHoldRepository;
    private final StockRepository stockRepository;
    private final UserTradesRepository userTradesRepository;
    private final UserTrackingRepository userTrackingRepository;
    private final StockViewBatchRepository stockViewBatchRepository;
    private final StockVolumeBatchRepository stockVolumeBatchRepository;
    private final Random random = new Random();

    private List<UserTrades> getUserTradesByInvestmentType(Side side, InvestmentType investmentType) {
        List<UserTrades> allTrades = new ArrayList<>();
        List<UserTrades> userTrades;

        if (investmentType == InvestmentType.HOLD) {
            List<GuruHold> holds = guruHoldRepository.findAll();
            for (GuruHold hold : holds) {
                userTrades = userTradesRepository.findByUser_IdAndSide(hold.getGuruUserId(), side);
                allTrades.addAll(userTrades);
            }
        } else if (investmentType == InvestmentType.SWING) {
            List<GuruSwing> swings = guruSwingRepository.findAll();
            for (GuruSwing swing : swings) {
                userTrades = userTradesRepository.findByUser_IdAndSide(swing.getGuruUserId(), side);
                allTrades.addAll(userTrades);
            }
        } else if (investmentType == InvestmentType.DAY) {
            List<GuruDay> dailies = guruDayRepository.findAll();
            for (GuruDay daily : dailies) {
                userTrades = userTradesRepository.findByUser_IdAndSide(daily.getGuruUserId(), side);
                allTrades.addAll(userTrades);
            }
        }
        return allTrades;
    }

    private List<String> getGuruUserIdsByInvestmentType(InvestmentType investmentType) {
        return switch (investmentType) {
            case HOLD -> guruHoldRepository.findAll().stream()
                    .map(GuruHold::getGuruUserId).toList();
            case SWING -> guruSwingRepository.findAll().stream()
                    .map(GuruSwing::getGuruUserId).toList();
            case DAY -> guruDayRepository.findAll().stream()
                    .map(GuruDay::getGuruUserId).toList();
        };
    }


    public List<Stock> getTop10ByStockAndQuantity(List<UserTrades> allTrades) {
        Map<Stock, Double> stockVolumeMap = allTrades.stream()
                .collect(Collectors.groupingBy(
                        UserTrades::getStock,
                        Collectors.summingDouble(UserTrades::getQuantity)
                ));

        return stockVolumeMap.entrySet().stream()
                .sorted(Map.Entry.<Stock, Double>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .toList();
    }


    @Transactional
    public void batchVolume() {
        // 투자 유형별, Side별 상위 10개 종목 계산
        for (InvestmentType type : InvestmentType.values()) {
            for (Side side : Side.values()) {
                List<UserTrades> trades = getUserTradesByInvestmentType(side, type);
                List<Stock> topStocks = getTop10ByStockAndQuantity(trades);

                List<StockVolumeBatch> batchList = topStocks.stream()
                        .map(stock -> new StockVolumeBatch(stock.getId(), side, type))
                        .toList();

                // 기존 배치 삭제 후 insert 또는 임시 테이블 rename 방식 추천
                stockVolumeBatchRepository.deleteBySideAndStockPeriod(side, type);
                stockVolumeBatchRepository.saveAll(batchList);
            }
        }
    }

    @Transactional
    public void guruTrackingViewBatch() {

        for (InvestmentType type : InvestmentType.values()) {
            List<String> guruUserIds = getGuruUserIdsByInvestmentType(type);
            List<UserTracking> trackings = userTrackingRepository.findByUserIdIn(guruUserIds);

            Map<String, BigDecimal> stockScores = trackings.stream()
                    .collect(Collectors.groupingBy(
                            UserTracking::getStockId,
                            Collectors.reducing(BigDecimal.ZERO,
                                    ut -> ut.getScore() == null ? BigDecimal.ZERO : BigDecimal.valueOf(ut.getScore()),
                                    BigDecimal::add)
                    ));

            List<String> topStockIds = stockScores.entrySet().stream()
                    .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                    .limit(15)
                    .map(Map.Entry::getKey)
                    .toList();

            List<StockViewBatch> batchList = topStockIds.stream()
                    .map(stockId -> new StockViewBatch(stockId, type))
                    .toList();

            stockViewBatchRepository.deleteByStockPeriod(type);
            stockViewBatchRepository.saveAll(batchList);
        }
    }

    @Transactional
    public void userTrackingData() {

        List<Stock> allStocks = stockRepository.findAll();
        if (allStocks.isEmpty()) return;

        generateForInvestmentType(guruDayRepository.findAll(), allStocks);
        generateForInvestmentType(guruSwingRepository.findAll(), allStocks);
        generateForInvestmentType(guruHoldRepository.findAll(), allStocks);
    }

    private void generateForInvestmentType(List<?> gurus, List<Stock> stocks) {
        for (Object guruObj : gurus) {
            String userId;

            if (guruObj instanceof GuruDay gd) {
                userId = gd.getGuruUserId();
            } else if (guruObj instanceof GuruSwing gs) {
                userId = gs.getGuruUserId();
            } else if (guruObj instanceof GuruHold gh) {
                userId = gh.getGuruUserId();
            } else {
                continue;
            }

            // 랜덤 Stock 선택
            Stock stock = stocks.get(random.nextInt(stocks.size()));

            // 랜덤 deltaScore 생성 (0.1 ~ 5.0)
            double deltaScore = 0.1 + (5.0 - 0.1) * random.nextDouble();

            long now = Instant.now().getEpochSecond();
            long randomTime = now - random.nextInt(24 * 3600);
            Instant eventTime = Instant.ofEpochSecond(randomTime);

            UserTracking tracking = new UserTracking();
            tracking.setUserTrackingId(UUID.randomUUID().toString());
            tracking.setUserId(userId);
            tracking.setStockId(stock.getId());
            tracking.setScore(deltaScore);
            tracking.setEventTime(eventTime);

            userTrackingRepository.save(tracking);
        }
    }

}
