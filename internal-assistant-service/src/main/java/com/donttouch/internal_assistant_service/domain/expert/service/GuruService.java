package com.donttouch.internal_assistant_service.domain.expert.service;

import com.donttouch.common_service.auth.entity.InvestmentType;
import com.donttouch.common_service.stock.entity.Stock;
import com.donttouch.common_service.stock.repository.StockRepository;
import com.donttouch.common_service.stock.repository.UserStocksRepository;
import com.donttouch.internal_assistant_service.domain.expert.entity.GuruTradeData;
import com.donttouch.internal_assistant_service.domain.expert.entity.vo.GuruTradeResponse;
import com.donttouch.internal_assistant_service.domain.expert.entity.vo.GuruVolumeRankDto;
import com.donttouch.internal_assistant_service.domain.expert.entity.vo.GuruVolumeResponse;
import com.donttouch.internal_assistant_service.domain.expert.repository.GuruDayRepository;
import com.donttouch.internal_assistant_service.domain.exception.ErrorMessage;
import com.donttouch.internal_assistant_service.domain.exception.StockNotFoundException;
import com.donttouch.internal_assistant_service.domain.member.entity.Side;
import com.donttouch.internal_assistant_service.domain.member.repository.UserTradesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import com.donttouch.common_service.auth.entity.User;
import com.donttouch.common_service.auth.entity.vo.MyInfoResponse;
import com.donttouch.common_service.auth.repository.UserRepository;
import com.donttouch.common_service.global.aop.dto.CurrentMemberIdRequest;
import com.donttouch.internal_assistant_service.domain.expert.entity.vo.UserTrackingRequest;
import com.donttouch.internal_assistant_service.domain.expert.entity.vo.UserTrackingResponse;
import com.donttouch.internal_assistant_service.domain.member.exception.ErrorMessage;
import com.donttouch.internal_assistant_service.domain.member.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

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
    private final GuruDayRepository guruSwingRepository;
    private final GuruDayRepository guruHoldRepository;

    public GuruTradeResponse getGuruTrade(String symbol, InvestmentType type) {
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new StockNotFoundException(ErrorMessage.STOCK_NOT_FOUND));

        List<String> guruUserIds = switch (type) {
            case DAY -> guruDayRepository.findAllUserIds();
            case SWING -> guruSwingRepository.findAllUserIds();
            case HOLD -> guruHoldRepository.findAllUserIds();
        };
        List<GuruTradeData> tradeStats = userTradesRepository.aggregateDailyTradeStats(guruUserIds, stock.getId());
        Double totalHolding = userStocksRepository.sumTotalHoldings(guruUserIds, stock.getId());

        return GuruTradeResponse.of(stock, type, tradeStats, totalHolding);
    }

//    public List<GuruVolumeResponse> getTopVolumeStocks(Side trade, InvestmentType type) {
//        List<String> guruUserIds = switch (type) {
//            case DAY -> guruDayRepository.findAllUserIds();
//            case SWING -> guruSwingRepository.findAllUserIds();
//            case HOLD -> guruHoldRepository.findAllUserIds();
//        };
//
//        List<GuruVolumeRankDto> topList = userTradesRepository.findTopGuruVolume(guruUserIds, trade, PageRequest.of(0, 10));
//
//        List<GuruVolumeResponse> responses = new ArrayList<>();
//
//        for (GuruVolumeRankDto dto : topList) {
//
//            var price = dailyStockChartsService.getPrePreviousClosePrice(dto.getStockSymbol());
//
//            Double prevClose = price.getPreviousClosePrice();
//            Double prePrevClose = price.getPrePreviousClosePrice();
//
//            double closeChangeRate = 0;
//            if (prePrevClose != null && prePrevClose != 0) {
//                closeChangeRate = (prevClose - prePrevClose) / prePrevClose * 100;
//            }
//
//            Double prevVolume = dto.getPrevVolume();
//            Double prePrevVolume = dto.getPrevPrevVolume();
//            double volumeChangeRate = dto.getVolumeChangeRate();
//
//            GuruVolumeResponse response = GuruVolumeResponse.builder()
//                    .stockId(dto.getStockId())
//                    .stockSymbol(dto.getStockSymbol())
//                    .stockName(dto.getStockName())
//                    .prevClose(prevClose)
//                    .prevPrevClose(prePrevClose)
//                    .closeChangeRate(closeChangeRate)
//                    .prevVolume(prevVolume)
//                    .prevPrevVolume(prePrevVolume)
//                    .volumeChangeRate(volumeChangeRate)
//                    .build();
//
//            responses.add(response);
//        }
//
//        return responses;
//    }

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final StringRedisTemplate trackingRedisTemplate;
    private final UserRepository userRepository;

    private static final String KEY_PREFIX = "ut:USER:";

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


    public MyInfoResponse getMyInfo(CurrentMemberIdRequest currentMemberIdRequest) {

        User user = userRepository.findById(currentMemberIdRequest.getUserUuid()).
                orElseThrow(()-> new UserNotFoundException(ErrorMessage.USER_NOT_FOUND));

        return MyInfoResponse.builder()
                .id(user.getId())
                .investmentType(user.getInvestmentType())
                .name(user.getName())
                .build();
    }
}
