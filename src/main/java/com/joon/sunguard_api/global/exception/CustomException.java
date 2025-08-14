package com.joon.sunguard_api.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String additionalMessage;

    public CustomException(ErrorCode errorCode){
        this.errorCode = errorCode;
        this.additionalMessage = null;
    }

    public CustomException(ErrorCode errorCode, String additionalMessage) {
        super(errorCode.getMessage() + " - " + additionalMessage);
        this.errorCode = errorCode;
        this.additionalMessage = additionalMessage;
    }

    // 원인 예외까지 포함
    public CustomException(ErrorCode errorCode, String additionalMessage, Throwable cause) {
        super(errorCode.getMessage() + " - " + additionalMessage, cause);
        this.errorCode = errorCode;
        this.additionalMessage = additionalMessage;
    }
}
