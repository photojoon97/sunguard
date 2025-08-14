
// =================================================================
// 설명 : gScore와 cameFrom의 Key로 사용되는 '상태' 정보 (어떤 정류장, 어떤 노선, 몇 번 환승했는지)
// =================================================================
package com.joon.sunguard_api.domain.route.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode // Map의 Key로 사용하기 위해 필수
public class PathSegment {
    private final String stopId; // 현재 정류장
    private final String lineId; // 어떤 버스를 타고 왔는지
    private final int transfers; //현재까지 환승 횟수

}
