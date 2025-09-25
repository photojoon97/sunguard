package com.joon.sunguard_api.domain.route.service;

import com.joon.sunguard_api.domain.busstop.entity.BusStop;

import com.joon.sunguard_api.domain.route.util.CalculateDirection;
import com.joon.sunguard_api.domain.route.util.CalculateDistance;
import com.joon.sunguard_api.domain.route.util.Directions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class AstarPathfinding implements Pathfinder {

    private final RouteDataLoader routeDataLoader;
    private final CalculateDistance calculateDistance;
    private final CalculateDirection calculateDirection;

    private static final double TRANSFER_PENALTY = 1.0; // 환승 페널티 (km)

    @Override
    public List<Node> findRoute(String startStopId, String endStopId) {

        Map<String, List<String>> nightlineToStops = routeDataLoader.getNightlineToStops();

        //context 초기화
        BusStop startStop = routeDataLoader.getStopInfo().get(startStopId);
        BusStop endStop = routeDataLoader.getStopInfo().get(endStopId);

        List<String> startStopList = routeDataLoader.getStopNameToIds().get(startStop.getStopName());
        List<String> endStopList = routeDataLoader.getStopNameToIds().get(endStop.getStopName());

        PathfindingContext context = new PathfindingContext(startStopId, endStopId);
        context.allStartStopIds.addAll(startStopList);
        context.allEndStopIds.addAll(endStopList);

        for (String curStopId : context.allStartStopIds) {
            BusStop curBusStop = routeDataLoader.getStopInfo().get(curStopId);

            for (String curLindId : routeDataLoader.getStopToLines().get(curStopId)) {
                //Node 객체 생성
                double h = heuristic(curBusStop, endStop);
                Node node = new Node(0.0 + h, 0.0, curStopId, curLindId, 0.0, null, 0);
                context.getGScore().put(node, node.getGScore());

                context.getOpenSet().add(node);
                context.getCameFrom().put(node, null);
            }
        }

        while (!context.getOpenSet().isEmpty()) {
            //경로 탐색 시작

            Node cur = context.getOpenSet().poll();
            String lineId = cur.getLineId();

            //TODO : 우선, 심야 노선은 스킵 처리했음
            //       추후 시간대별 노선 제공
            if (nightlineToStops.containsKey(lineId)) {
                continue;
            }

            BusStop curStop = routeDataLoader.getStopInfo().get(cur.getStopId());

            //도착
            if (checkGoalNode(context, cur)) {
                return reconstructPath(context.getCameFrom(), cur);
            }

            //인접 정류장들을 방문해야 함
            List<String> stops = routeDataLoader.getLineToStops().get(lineId);
            int curIdx = stops.indexOf(cur.getStopId());

            if (curIdx != -1 && curIdx < stops.size() - 1) {
                String nextStopId = stops.get(curIdx + 1);
                BusStop nextStop = routeDataLoader.getStopInfo().get(nextStopId);

                double distance = calculateDistance.getDistnace
                        (curStop.getGpsY(), curStop.getGpsX(),
                                nextStop.getGpsY(), nextStop.getGpsX());

                Directions direction = calculateDirection.getDirection(curStop.getGpsY(), curStop.getGpsX(),
                        nextStop.getGpsY(), nextStop.getGpsX());

                double tentativeG = cur.getGScore() + distance;
                double h = heuristic(nextStop, endStop);


                Node neighborNode = new Node(tentativeG + h, tentativeG, nextStopId, lineId, distance, direction, cur.getTransfers());

                if (!context.getGScore().containsKey(neighborNode) || tentativeG < context.getGScore().get(neighborNode)) {

                    context.getCameFrom().put(neighborNode, cur);
                    context.getGScore().put(neighborNode, tentativeG);
                    context.getOpenSet().add(neighborNode);

                }

            }
            transfer(context, cur, endStop);
        }
        return null;
    }


    public void transfer(PathfindingContext context, Node cur, BusStop endStop) {
        if (cur.getTransfers() < context.getMAX_TRANSFER()) {
            List<String> lines = routeDataLoader.getStopToLines().get(cur.getStopId());

            for (String line : lines) {

                if (!line.equals(cur.getLineId())) {

                    double tentativeGScore = context.getGScore().get(cur) + TRANSFER_PENALTY;

                    Node neighborNode = new Node(0, tentativeGScore, cur.getStopId(), line, 0, null, cur.getTransfers() + 1);
                    if (tentativeGScore < context.getGScore().getOrDefault(neighborNode, Double.MAX_VALUE)) {
                        BusStop busStop = routeDataLoader.getStopInfo().get(cur.getStopId());
                        double h = heuristic(busStop, endStop);
                        neighborNode.setfScore(tentativeGScore + h);

                        context.getCameFrom().put(neighborNode, cur);
                        context.getGScore().put(neighborNode, tentativeGScore);
                        context.getOpenSet().add(neighborNode);
                    }
                }
            }
        }
    }

    public int findSeq(List<BusStop> stops, String stopId) {
        return 0;
    }

    public List<Node> reconstructPath(Map<Node, Node> cameFrom, Node cur) {
        List<Node> totalPath = new ArrayList<>();
        totalPath.add(cur); // 최종 목적지 노드를 먼저 추가

        Node current = cur;
        // cameFrom 맵을 따라가며 현재 노드(current)의 이전 노드를 찾습니다.
        // 이전 노드가 null이 될 때(출발지)까지 반복합니다.
        while (cameFrom.containsKey(current) && cameFrom.get(current) != null) {
            current = cameFrom.get(current);
            totalPath.add(current);
        }

        Collections.reverse(totalPath);
        return totalPath;
    }

    public double heuristic(BusStop stop, BusStop nextStop) {
        return calculateDistance.getDistnace(stop.getGpsY(), stop.getGpsX(), nextStop.getGpsY(), nextStop.getGpsX());
    }

    public boolean checkGoalNode(PathfindingContext context, Node cur) {
        if (context.getAllEndStopIds().contains(cur.getStopId())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkContainGoalNode(PathfindingContext context, Node cur) {
        return false;
    }
}
