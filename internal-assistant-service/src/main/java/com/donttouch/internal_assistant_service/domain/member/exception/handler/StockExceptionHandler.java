package com.donttouch.internal_assistant_service.domain.member.exception.handler;
import com.donttouch.internal_assistant_service.domain.member.exception.AssetNotFoundException;
import com.donttouch.internal_assistant_service.domain.member.exception.ChartDataNotFoundException;
import com.donttouch.internal_assistant_service.domain.member.exception.StockNotFoundException;
import com.donttouch.internal_assistant_service.domain.member.exception.UserNotFoundException;
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

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "error", e.getMessage(),
                        "status", HttpStatus.NOT_FOUND.value()
                ));
    }

    @ExceptionHandler(AssetNotFoundException.class)
    public ResponseEntity<Object> handleAssetNotFoundException(AssetNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "error", e.getMessage(),
                        "status", HttpStatus.NOT_FOUND.value()
                ));
    }
}
