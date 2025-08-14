package com.joon.sunguard_api.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    BUSSTOP_NOT_FOUND(HttpStatus.NOT_FOUND, "정류장을 찾을 수 없습니다."),
    ROUTE_NOT_FOUND(HttpStatus.NOT_FOUND, "경로를 찾을 수 없습니다."),
    DUPLICATED_REQUEST(HttpStatus.BAD_REQUEST, "중복된 요청입니다."),
    BUS_NOT_FOUND(HttpStatus.NOT_FOUND, "버스를 찾을 수 없습니다."),
    API_CALL_ERROR(HttpStatus.BAD_GATEWAY, "API 호출 실패."),
    XML_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "XML 파싱 실패."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 오류.");


    private final HttpStatus httpStatus;
    private final String message;
}
