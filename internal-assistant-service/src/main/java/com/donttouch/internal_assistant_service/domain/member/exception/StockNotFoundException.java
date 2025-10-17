package com.donttouch.internal_assistant_service.domain.member.exception;

public class StockNotFoundException extends RuntimeException{
    public StockNotFoundException(ErrorMessage message) {
        super(String.valueOf(message));
    }
}
