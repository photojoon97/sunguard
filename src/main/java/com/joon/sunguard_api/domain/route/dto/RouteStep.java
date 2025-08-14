package com.joon.sunguard_api.domain.route.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class RouteStep {
    // 버스 정보
    private String lineNum;
    private String lineId;

    // 승차 정류장 정보
    private String startStopName;
    private String startStopId;

    // 하차 정류장 정보
    private String endStopName;
    private String endStopId;

    // 이동한 정류장 수
    private int stopCount;

    // 해당 구간의 모든 정류장 이름 목록
    private List<String> stops;
}