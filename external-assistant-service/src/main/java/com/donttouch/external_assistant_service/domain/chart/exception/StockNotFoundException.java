package com.donttouch.external_assistant_service.domain.chart.exception;

public class StockNotFoundException extends RuntimeException{
    public StockNotFoundException(ErrorMessage message) {
        super(String.valueOf(message));
    }
}
