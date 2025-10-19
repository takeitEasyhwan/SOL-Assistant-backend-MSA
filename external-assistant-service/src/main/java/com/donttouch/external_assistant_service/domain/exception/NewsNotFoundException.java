package com.donttouch.external_assistant_service.domain.exception;

public class NewsNotFoundException extends RuntimeException{
    public NewsNotFoundException(ErrorMessage message) {
        super(String.valueOf(message));
    }
}
