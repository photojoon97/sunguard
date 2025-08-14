package com.joon.sunguard_api.global.exception;

import lombok.Builder;
import org.springframework.http.ResponseEntity;

@Builder
public class ErrorResponseEntity {
        private int status;
        private String name;
        private String code;
        private String message;

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(CustomException customException) {
        ErrorCode e = customException.getErrorCode();
        String finalMessage = (customException.getAdditionalMessage() != null && !customException.getAdditionalMessage().trim().isEmpty())
                ? e.getMessage() + " - " + customException.getAdditionalMessage()
                : customException.getMessage();

        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ErrorResponseEntity.builder()
                        .status(e.getHttpStatus().value())
                        .name(e.name())
                        .message(finalMessage)
                        .build());
    }

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(ErrorCode e, String message) {
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ErrorResponseEntity.builder()
                        .status(e.getHttpStatus().value())
                        .name(e.name())
                        .message(e.getMessage() + " - " + message)
                        .build());
    }
}
