package com.donttouch.internal_assistant_service.domain.exception;

public class ChartDataNotFoundException extends RuntimeException{
    public ChartDataNotFoundException(ErrorMessage message) {
        super(String.valueOf(message));
    }
}
