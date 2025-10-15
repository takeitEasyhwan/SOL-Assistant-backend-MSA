package com.donttouch.internal_assistant_service.domain.member.exception;

public class ChartDataNotFoundException extends RuntimeException{
    public ChartDataNotFoundException(ErrorMessage message) {
        super(String.valueOf(message));
    }
}
