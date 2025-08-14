package com.joon.sunguard_api.domain.busstop.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BusStopResponse {

    // --- 필수 필드 ---
    private final String stopName; // 정류장 이름
    private final String stopId;     // 정류장 ID (API 호출용)
    private final String stopNo;     // 정류장 번호 (사용자 확인용)

    // --- 선택 필드 ---
    private final Double distance;    // 현재 위치로부터의 거리 (단위: km)

    @Builder
    public BusStopResponse(String stopName, String stopId, String stopNo, Double distance) {
        this.stopName = stopName;
        this.stopId = stopId;
        this.stopNo = stopNo;
        this.distance = distance;
    }
}
