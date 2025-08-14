package com.joon.sunguard_api.domain.busstop.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
public class BusArrivalResponse {

    // --- 필수 필드 ---
    private final String lineId;        // 노선 ID
    private final String lineNo;        // 버스 번호
    private final String remainingTime; // 도착까지 남은 시간 (e.g., "5분")
    private final String remainingStops;// 남은 정류장 수 (e.g., "3개")

    @Builder
    public BusArrivalResponse(@NonNull String lineId, @NonNull String lineNo, @NonNull String remainingTime, @NonNull String remainingStops) {
        this.lineId = lineId;
        this.lineNo = lineNo;
        this.remainingTime = remainingTime;
        this.remainingStops = remainingStops;
    }
}
