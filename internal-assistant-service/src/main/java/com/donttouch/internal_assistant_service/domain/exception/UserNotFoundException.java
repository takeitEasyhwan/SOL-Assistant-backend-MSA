package com.donttouch.internal_assistant_service.domain.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(ErrorMessage message) {
        super(String.valueOf(message));
    }
}
