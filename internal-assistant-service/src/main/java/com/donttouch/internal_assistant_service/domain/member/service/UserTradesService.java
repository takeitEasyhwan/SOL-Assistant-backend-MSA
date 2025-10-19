package com.donttouch.internal_assistant_service.domain.member.service;

import com.donttouch.common_service.auth.entity.User;
import com.donttouch.common_service.auth.repository.UserRepository;
import com.donttouch.common_service.stock.entity.Stock;
import com.donttouch.common_service.stock.repository.StockRepository;
import com.donttouch.common_service.stock.entity.UserStocks;
import com.donttouch.internal_assistant_service.domain.member.entity.UserAssets;
import com.donttouch.internal_assistant_service.domain.member.entity.UserTrades;
import com.donttouch.internal_assistant_service.domain.member.entity.vo.TradeRequest;
import com.donttouch.internal_assistant_service.domain.member.entity.vo.TradeResponse;
import com.donttouch.internal_assistant_service.domain.exception.ErrorMessage;
import com.donttouch.internal_assistant_service.domain.exception.StockNotFoundException;
import com.donttouch.internal_assistant_service.domain.exception.UserNotFoundException;
import com.donttouch.common_service.stock.repository.UserStocksRepository;
import com.donttouch.internal_assistant_service.domain.member.repository.UserAssetsRepository;
import com.donttouch.internal_assistant_service.domain.member.repository.UserTradesRepository;
import jakarta.transaction.Transactional;
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
    private final UserStocksRepository userStocksRepository;
    private final UserAssetsRepository userAssetsRepository;

    @Transactional
    public TradeResponse buy(TradeRequest request) {
        Stock stock = stockRepository.findBySymbol(request.getSymbol())
                .orElseThrow(() -> new StockNotFoundException(ErrorMessage.STOCK_NOT_FOUND));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException(ErrorMessage.USER_NOT_FOUND));

        UserAssets userAssets = userAssetsRepository.findByUserId(user.getId())
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

        double totalBuyCost = request.getQuantity() * request.getPrice();
        if (userAssets.getTotalBalance() < totalBuyCost) {
            return TradeResponse.builder()
                    .status("FAIL - INSUFFICIENT_BALANCE")
                    .userId(user.getId())
                    .stockId(stock.getId())
                    .build();
        }

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
        double buyQty = request.getQuantity();
        double buyPrice = request.getPrice();

        if (userStock == null) {
            userStock = new UserStocks();
            userStock.setUserStockId(UUID.randomUUID().toString());
            userStock.setUser(user);
            userStock.setStock(stock);
            userStock.setQuantity(buyQty);
            userStock.setCostBasis(buyPrice);
        } else {
            double oldQty = userStock.getQuantity();
            double oldAvg = userStock.getCostBasis();
            double newQty = oldQty + buyQty;
            double newAvg = ((oldQty * oldAvg) + (buyQty * buyPrice)) / newQty;

            userStock.setQuantity(newQty);
            userStock.setCostBasis(newAvg);
        }
        userStocksRepository.save(userStock);

        userAssets.setTotalBalance(userAssets.getTotalBalance() - totalBuyCost);
        userAssetsRepository.save(userAssets);

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

    @Transactional
    public TradeResponse sell(TradeRequest request) {
        Stock stock = stockRepository.findBySymbol(request.getSymbol())
                .orElseThrow(() -> new StockNotFoundException(ErrorMessage.STOCK_NOT_FOUND));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException(ErrorMessage.USER_NOT_FOUND));

        UserAssets userAssets = userAssetsRepository.findByUserId(user.getId())
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

        UserStocks userStock = userStocksRepository.findByUserAndStock(user, stock).orElse(null);

        if (userStock == null) {
            return TradeResponse.builder()
                    .status("FAIL - NO_OWNED_STOCK")
                    .userId(user.getId())
                    .stockId(stock.getId())
                    .build();
        }

        double sellQty = request.getQuantity();
        double sellPrice = request.getPrice();
        double currentQty = userStock.getQuantity();

        if (currentQty < sellQty) {
            return TradeResponse.builder()
                    .status("FAIL - INSUFFICIENT_QUANTITY")
                    .userId(user.getId())
                    .stockId(stock.getId())
                    .build();
        }

        String tradeId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        UserTrades trade = UserTrades.builder()
                .userTradeId(tradeId)
                .user(user)
                .stock(stock)
                .tradeTs(now)
                .quantity(sellQty)
                .price(sellPrice)
                .side(request.getSide())
                .build();

        userTradesRepository.save(trade);

        double newQty = currentQty - sellQty;
        if (newQty <= 0) {
            userStocksRepository.delete(userStock);
        } else {
            userStock.setQuantity(newQty);
            userStocksRepository.save(userStock);
        }

        double totalSellAmount = sellQty * sellPrice;
        userAssets.setTotalBalance(userAssets.getTotalBalance() + totalSellAmount);
        userAssetsRepository.save(userAssets);

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
