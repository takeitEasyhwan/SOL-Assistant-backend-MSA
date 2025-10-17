package com.donttouch.external_assistant_service.domain.chart.exception;

public class ChartDataNotFoundException extends RuntimeException{
    public ChartDataNotFoundException(ErrorMessage message) {
        super(String.valueOf(message));
    }
}
