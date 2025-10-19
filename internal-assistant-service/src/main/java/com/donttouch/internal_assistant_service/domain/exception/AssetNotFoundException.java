package com.donttouch.internal_assistant_service.domain.exception;

public class AssetNotFoundException extends RuntimeException {
    public AssetNotFoundException(ErrorMessage message) {
        super(String.valueOf(message));
    }
}
