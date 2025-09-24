
// =================================================================
// 설명 : openSet에 들어가는 데이터로, 총점(fScore)을 가지고 있어서 우선순위 비교에 사용
// =================================================================
package com.joon.sunguard_api.domain.route.dto;

import com.joon.sunguard_api.domain.route.util.Directions;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class RouteNode implements Comparable<RouteNode> {
    private final double fScore; // G-Score(현재까지 비용) + H-Score(예상 잔여 비용) (PriorityQueue에서의 기준값)
    private final double gScore; // 실제 이동 비용
    private final String stopId;
    private final String lineId;
    private final int transfers; // 환승 횟수
    private final double distance; //역 사이 이동 거리
    private final Directions direction; //역 사이 진행방향

    @Override
    public int compareTo(RouteNode other) {
        return Double.compare(this.fScore, other.fScore);
    }
}
