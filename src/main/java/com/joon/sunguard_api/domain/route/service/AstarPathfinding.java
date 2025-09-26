package com.joon.sunguard_api.domain.route.service;

import com.joon.sunguard_api.domain.busstop.entity.BusStop;
import com.joon.sunguard_api.domain.route.dto.RouteResponse;
import com.joon.sunguard_api.domain.route.util.CalculateDirection;
import com.joon.sunguard_api.domain.route.util.CalculateDistance;
import com.joon.sunguard_api.domain.route.util.Directions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;


import java.util.*;

//TODO : 1. 직행 노선 조기 종료 구현
//       2. 도보 환승 구현
//       3. 환승 후보지간에 우선순위 구현(샘플링 기법)
//       4. openSet 최적화 (불필요한 Node는 삭제)

@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class AstarPathfinding implements Pathfinder {

    private final RouteDataLoader routeDataLoader;
    private final CalculateDistance calculateDistance;
    private final CalculateDirection calculateDirection;

    private static final double TRANSFER_PENALTY = 1.0; // 환승 페널티 (km)
    private static final int MAX_TRANSFER = 2;

    @Override
    public RouteResponse findRoute(String startStopId, String endStopId) {

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
                String curStopName = curBusStop.getStopName();
                String busNo = routeDataLoader.getLineInfo().get(curLindId);
                Node node = new Node(0.0 + h, 0.0, curStopId, curStopName, curLindId, busNo, 0.0, null, 0);
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
                String nextStopName = nextStop.getStopName();
                String busNo = routeDataLoader.getLineInfo().get(lineId);

                double distance = calculateDistance.getDistnace
                        (curStop.getGpsY(), curStop.getGpsX(),
                                nextStop.getGpsY(), nextStop.getGpsX());

                Directions direction = calculateDirection.getDirection(curStop.getGpsY(), curStop.getGpsX(),
                        nextStop.getGpsY(), nextStop.getGpsX());

                double tentativeG = cur.getGScore() + distance;
                double h = heuristic(nextStop, endStop);


                Node neighborNode = new Node(tentativeG + h, tentativeG, nextStopId, nextStopName, lineId, busNo, distance, direction, cur.getTransfers());

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
        int transfer = cur.getTransfers() + 1;
        if (transfer <= MAX_TRANSFER) {
            List<String> lines = routeDataLoader.getStopToLines().get(cur.getStopId());

            for (String line : lines) {

                if (!line.equals(cur.getLineId())) {

                    String busNo = routeDataLoader.getLineInfo().get(line);
                    double tentativeGScore = context.getGScore().get(cur) + TRANSFER_PENALTY;

                    Node neighborNode = new Node(0, tentativeGScore, cur.getStopId(), cur.getStopName(), line, busNo, 0.0, null, transfer);
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

    public RouteResponse reconstructPath(Map<Node, Node> cameFrom, Node cur) {
        List<Node> totalPath = new ArrayList<>();
        Map<Directions, Double> totalDirection = new HashMap<>();
        double totalDistance = 0.0;
        Directions mostDirection = null;
        int totalTransfer = 0;

        totalPath.add(cur); // 최종 목적지 노드 추가
        Node current = cur;

        // 출발지까지 역추적
        while (cameFrom.containsKey(current) && cameFrom.get(current) != null) {
            Double tempDistance = current.getDistance();
            Directions tempDirec = current.getDirection();

            totalDistance += tempDistance; // 👉 총 이동거리 누적
            totalTransfer += current.getTransfers(); // 👉 환승 횟수 누적

            current = cameFrom.get(current);
            totalPath.add(current);

            totalDirection.compute(tempDirec, (key, value) ->
                    value == null ? tempDistance : value + tempDistance
            );
        }

        Collections.reverse(totalPath);

        // 가장 많이 이동한 방향 계산
        Optional<Map.Entry<Directions, Double>> max =
                totalDirection.entrySet().stream().max(Map.Entry.comparingByValue());

        if (max.isPresent()) {
            Map.Entry<Directions, Double> m = max.get();
            mostDirection = m.getKey();
        }

        return RouteResponse.builder()
                .steps(totalPath)
                .totalDirection(mostDirection)
                .totalDistance(totalDistance)
                .transferCount(totalTransfer)
                .build();
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

    public int findSeq(List<BusStop> stops, String stopId) {
        return 0;
    }
}
