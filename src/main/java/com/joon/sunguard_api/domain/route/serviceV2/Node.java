package com.joon.sunguard_api.domain.route.serviceV2;

import com.joon.sunguard_api.domain.route.util.Directions;
import lombok.*;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class Node {

    private double fScore; // G-Score(현재까지 비용) + H-Score(예상 잔여 비용) (PriorityQueue에서의 기준값)
    private final double gScore; // 실제 이동 비용
    private final String stopId;
    private final String lineId;
    private final double distance; //역 사이 이동 거리
    private final Directions direction;
    private final int transfers;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;

        return transfers ==
                node.transfers &&
                Objects.equals(stopId, node.stopId) &&
                Objects.equals(lineId, node.lineId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stopId, lineId, transfers);
    }

    public void setfScore(double fScore) {
        this.fScore = fScore;
    }
}
