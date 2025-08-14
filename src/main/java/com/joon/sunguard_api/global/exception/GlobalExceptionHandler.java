package com.joon.sunguard_api.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponseEntity> handleException(CustomException e){
        return ErrorResponseEntity.toResponseEntity(e);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ErrorResponseEntity> handleIllegalArgumentException(IllegalArgumentException e){
        log.warn("잘못된 인자: {}", e.getMessage());
        return ErrorResponseEntity.toResponseEntity(
                ErrorCode.INVALID_INPUT,
                e.getMessage()
        );
    }

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<ErrorResponseEntity> handleRuntimeException(RuntimeException e) {
        log.error("RuntimeException 처리 (예상하지 못한 런타임 오류): {}", e.getMessage(), e);
        return ErrorResponseEntity.toResponseEntity(ErrorCode.INTERNAL_SERVER_ERROR, "런타임 오류가 발생했습니다");
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponseEntity> handleException(Exception e) {
        log.error("Exception 처리 (모든 예외의 최종 처리): {}", e.getClass().getSimpleName(), e);
        log.error("예외 상세: {}", e.getMessage(), e);

        return ErrorResponseEntity.toResponseEntity(
                ErrorCode.INTERNAL_SERVER_ERROR,
                "예상하지 못한 서버 오류가 발생했습니다"
        );
    }
}
