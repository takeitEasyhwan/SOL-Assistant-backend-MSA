package com.donttouch.internal_assistant_service.domain.member.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(ErrorMessage message) {
        super(String.valueOf(message));
    }
}
