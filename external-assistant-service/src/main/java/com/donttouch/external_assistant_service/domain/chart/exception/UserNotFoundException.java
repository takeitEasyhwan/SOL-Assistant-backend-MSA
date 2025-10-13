package com.donttouch.external_assistant_service.domain.chart.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(ErrorMessage message) {
        super(String.valueOf(message));
    }
}
