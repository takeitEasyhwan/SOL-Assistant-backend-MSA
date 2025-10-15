package com.donttouch.internal_assistant_service.domain.member.exception;

public enum ErrorMessage {
    STOCK_NOT_FOUND("존재하지 않는 종목입니다."),
    CHART_DATA_NOT_FOUND("해당 종목의 차트 데이터가 없습니다."),
    USER_NOT_FOUND("존재하지 않는 사용자입니다.");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
