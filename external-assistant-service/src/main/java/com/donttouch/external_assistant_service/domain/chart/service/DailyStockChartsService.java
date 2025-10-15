package com.donttouch.external_assistant_service.domain.chart.service;

import com.donttouch.common_service.stock.entity.DailyStockCharts;
import com.donttouch.common_service.stock.entity.Stock;
import com.donttouch.common_service.stock.repository.StockRepository;
import com.donttouch.external_assistant_service.domain.chart.entity.vo.DailyPriceResponse;
import com.donttouch.common_service.stock.entity.vo.DailyStockChartsResponse;
import com.donttouch.external_assistant_service.domain.chart.entity.vo.StockRiskResponse;
import com.donttouch.external_assistant_service.domain.chart.exception.ChartDataNotFoundException;
import com.donttouch.external_assistant_service.domain.chart.exception.ErrorMessage;
import com.donttouch.external_assistant_service.domain.chart.exception.StockNotFoundException;
import com.donttouch.external_assistant_service.domain.chart.repository.DailyStockChartsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyStockChartsService {
    private final DailyStockChartsRepository dailyStockChartsRepository;
    private final StockRepository stockRepository;

    public List<DailyStockChartsResponse> getDailyCharts(String symbol) {
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new StockNotFoundException(ErrorMessage.STOCK_NOT_FOUND));
        return dailyStockChartsRepository.findByStockOrderByCurrentDayDesc(stock)
                .stream()
                .map(DailyStockChartsResponse::fromEntity)
                .toList();
    }

    public DailyPriceResponse getPreviousClosePrice(String symbol) {
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new StockNotFoundException(ErrorMessage.STOCK_NOT_FOUND));

        DailyStockCharts latest = dailyStockChartsRepository
                .findTopByStockOrderByCurrentDayDesc(stock)
                .orElseThrow(() -> new ChartDataNotFoundException(ErrorMessage.CHART_DATA_NOT_FOUND));

        return DailyPriceResponse.of(stock.getSymbol(), stock.getStockName(), latest.getClosePrice());
    }

    public DailyPriceResponse getPrePreviousClosePrice(String symbol) {
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new StockNotFoundException(ErrorMessage.STOCK_NOT_FOUND));

        List<DailyStockCharts> chartList = dailyStockChartsRepository
                .findByStockOrderByCurrentDayDesc(stock);

        if (chartList.size() < 2) {
            throw new ChartDataNotFoundException(ErrorMessage.CHART_DATA_NOT_FOUND);
        }

        Double previousClose = chartList.get(0).getClosePrice();
        Double prePreviousClose = chartList.get(1).getClosePrice();

        return DailyPriceResponse.ofWithPrePrevious(stock.getSymbol(), stock.getStockName(), previousClose, prePreviousClose);
    }

        public StockRiskResponse getStockRisk(String symbol) {
            Stock stock = stockRepository.findBySymbol(symbol)
                    .orElseThrow(() -> new StockNotFoundException(ErrorMessage.STOCK_NOT_FOUND));

            return StockRiskResponse.fromEntity(stock);
        }
}
