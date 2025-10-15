package com.donttouch.chart_similarity_service.domain.my_stock.service;

import com.donttouch.chart_similarity_service.domain.my_stock.entity.UserStock;
import com.donttouch.chart_similarity_service.domain.my_stock.repository.UserStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyStockService {

    private final UserStockRepository userStockRepository;

    public List<UserStock> getMyStocks(String userId) {
        return userStockRepository.findByUserId(userId);
    }
}
