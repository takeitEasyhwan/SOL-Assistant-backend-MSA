package com.donttouch.external_assistant_service.domain.chart.exception.handler;

import com.donttouch.external_assistant_service.domain.chart.exception.ChartDataNotFoundException;
import com.donttouch.external_assistant_service.domain.chart.exception.StockNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class StockExceptionHandler {
    @ExceptionHandler(StockNotFoundException.class)
    public ResponseEntity<Object> handleStockNotFoundException(StockNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "error", e.getMessage(),
                        "status", HttpStatus.NOT_FOUND.value()
                ));
    }

    @ExceptionHandler(ChartDataNotFoundException.class)
    public ResponseEntity<Object> handlerChartDataNotFoundException(ChartDataNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "error", e.getMessage(),
                        "status", HttpStatus.NOT_FOUND.value()
                ));
    }
}
