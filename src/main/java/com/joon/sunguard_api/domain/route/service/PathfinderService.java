package com.joon.sunguard_api.domain.route.service;

import com.joon.sunguard_api.domain.busstop.entity.BusStop;
import com.joon.sunguard_api.domain.route.dto.PathSegment;
import com.joon.sunguard_api.domain.route.dto.RouteNode;
import com.joon.sunguard_api.domain.route.dto.RouteResponse;
import com.joon.sunguard_api.domain.route.dto.RouteStep;
import com.joon.sunguard_api.domain.route.util.AzimuthAngle;
import com.joon.sunguard_api.domain.route.util.CalculateDistance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PathfinderService {

    private final RouteDataService routeDataService;
    private final CalculateDistance calculateDistance;
    private final AzimuthAngle azimuthAngle;
    private static final int MAX_TRANSFERS = 2; // 환승횟수 제한
    private static final double TRANSFER_PENALTY = 1.0; // 환승 페널티 (km)

    //TODO :
    // 시나리오 1: 목적 정류장까지 가는 경로가 없다면, 도착 정류장의 GPS와 그 근처 정류장의 가장 가까운 정류장 GPS에 가도록 유도. 나머지는 도보
    // 시나리오 2: 출발정류장과 도착 정류장의 직선 거리를 보고 많이 어긋나는 경우 그 경로 사용 안 함. -> 비슷한 위치의 정류장을 추천하거나, 다른 경로 재탐색
    public RouteResponse findShortestPath(String startStopId, String endStopId) {
        log.info("findShortestPath 메서드 실행 (ID 기반): {} -> {}", startStopId, endStopId);
        PathfindingContext context = new PathfindingContext();
        initContext(context, startStopId, endStopId);
        initAlgo(context);

        while (!context.openSet.isEmpty()) { //openSet이 빌때까지 새로운 경로 탐색
            Node current = new Node();
            current.routeNode = context.openSet.poll();

            current.currentStopId = current.routeNode.getStopId();
            current.currentLindId = current.routeNode.getLineId();
            current.currentTransfer = current.routeNode.getTransfers();
            context.moveVector.merge(current.routeNode.getDirection(), current.routeNode.getDistance(), Double::sum);

            // currentSegment 초기화 - 이 부분이 중요합니다!
            current.currentSegment = new PathSegment(current.currentStopId, current.currentLindId, current.currentTransfer);

            List<String> routeStops = routeDataService.getLineToStops().get(current.currentLindId);
            BusStop currentStopInfo = routeDataService.getStopInfo().get(current.currentStopId);

            // Node에 정보 설정
            current.routeStops = routeStops;
            current.currentStopInfo = currentStopInfo;

            moveStops(context, current);

            if (current.routeNode.getGScore() > context.gScore.getOrDefault(current.currentSegment, Double.MAX_VALUE)) {
                continue;
            }

            // 4. [수정] 현재 정류장이 목적지 후보 중 하나인지 확인합니다.
            if (context.allEndStopIds.contains(current.currentStopId)) { // 도착 정류장ID들 중 현재 정류장ID와 일치하는 것이 있으면 탐색 종료
                String direction = checkEndStop(context);
                return reconstructPath(context.cameFrom, current.currentSegment, context.gScore.get(current.currentSegment), direction);
            }

            transferBus(current, context);
        }
        log.warn("경로를 찾을 수 없습니다: {} -> {}", context.startStopName, context.endStopName);
        return null; // 경로 없음
    }

    private RouteResponse reconstructPath(Map<PathSegment, PathSegment> cameFrom, PathSegment lastSegment, double totalDistance, String direction) {
        List<PathSegment> path = new ArrayList<>();
        PathSegment current = lastSegment;

        while (current != null) {
            path.add(current);
            current = cameFrom.get(current);
        }
        Collections.reverse(path);

        if (path.isEmpty()) return null;

        List<RouteStep> steps = new ArrayList<>(); //진행 노선의 경로 정보

        int i = 0;
        //같은 버스를 탄 구간끼리 그룹화
        // i: 현재 그룹의 시작점
        // j: i에서 출발해서, 버스 노선이 바뀌기 전까지 최대한 멀리 가보는 포인터
        while (i < path.size()) {
            PathSegment stepStartSegment = path.get(i);
            String currentLineId = stepStartSegment.getLineId();

            int j = i;
            while (j < path.size() && path.get(j).getLineId().equals(currentLineId)) { // 같은 LindId인 동안 실행
                j++;
            }
            PathSegment stepEndSegment = path.get(j - 1); //해당 노선의 마지막 세그먼트

            List<String> stopIdsInStep = new ArrayList<>(); //정류장 ID 목록
            for (int k = i; k < j; k++) { // 처음부터 해당 노선의 마지막 정류장이 나올때까지 반복
                // 환승 시 중복되는 정류장 ID를 피하기 위해 추가
                if (k > i && path.get(k).getStopId().equals(path.get(k - 1).getStopId())) {
                    continue;
                }
                stopIdsInStep.add(path.get(k).getStopId());
            }

            List<String> stopNamesInStep = stopIdsInStep.stream()
                    .map(stopId -> routeDataService.getStopInfo().get(stopId).getStopName())
                    .collect(Collectors.toList());

            RouteStep step = RouteStep.builder()
                    .lineId(currentLineId)
                    .lineNum(routeDataService.getLineInfo().get(currentLineId))
                    .startStopId(stepStartSegment.getStopId())
                    .startStopName(routeDataService.getStopInfo().get(stepStartSegment.getStopId()).getStopName())
                    .endStopId(stepEndSegment.getStopId())
                    .endStopName(routeDataService.getStopInfo().get(stepEndSegment.getStopId()).getStopName())
                    .stopCount(stopIdsInStep.size() > 1 ? stopIdsInStep.size() - 1 : 0)
                    .stops(stopNamesInStep)
                    .build();
            steps.add(step);

            i = j;
        }

        return RouteResponse.builder()
                .steps(steps)
                .transferCount(steps.size() - 1)
                .totalDistance(String.format("%.2f", totalDistance))
                .direction(direction)
                .build();
    }

    private void initContext(PathfindingContext context, String startStopId, String endStopId) {
        try {
            context.startStop = routeDataService.getStopInfo().get(startStopId);
            context.endStop = routeDataService.getStopInfo().get(endStopId);
            context.startStopName = context.startStop.getStopName();
            context.endStopName = context.endStop.getStopName();

            context.allStartStopIds = routeDataService.getStopNameToIds().get(context.startStopName);
            context.allEndStopIds = routeDataService.getStopNameToIds().get(context.endStopName);

            // 휴리스틱(h-score) 계산을 위해 대표 목적지 정류장
            context.representativeEndStop = context.endStop;

            context.openSet = new PriorityQueue<>();
            context.cameFrom = new HashMap<>();
            context.gScore = new HashMap<>();
            context.moveVector = new HashMap<>();
        } catch (RuntimeException e) {
            throw e;
        }
    }

    private void initAlgo(PathfindingContext context) {
        // 3. 가능한 모든 출발 정류장에 대해 A* 알고리즘을 초기화합니다.
        for (String currentStartId : context.allStartStopIds) {
            BusStop currentStartStop = routeDataService.getStopInfo().get(currentStartId);
            for (String lineId : routeDataService.getStopToLines().getOrDefault(currentStartId, Collections.emptyList())) {
                PathSegment startSegment = new PathSegment(currentStartId, lineId, 0);
                context.gScore.put(startSegment, 0.0);

                double hScore = calculateDistance.getDistance(
                        currentStartStop.getGpsY(), currentStartStop.getGpsX(),
                        context.representativeEndStop.getGpsY(), context.representativeEndStop.getGpsX()
                );

                context.openSet.add(new RouteNode(hScore, 0.0, currentStartId, lineId, 0, 0.0, ""));
                context.cameFrom.put(startSegment, null);
            }
        }
    }

    private void moveStops(PathfindingContext context, Node current) {
        // 5. 동일 노선으로 다음 정류장 이동
        if (current.routeStops != null) {
            int currentIndex = current.routeStops.indexOf(current.currentStopId);
            if (currentIndex != -1 && currentIndex < current.routeStops.size() - 1) { //현재 위치가 종점이 아닌 경우
                String nextStopId = current.routeStops.get(currentIndex + 1); //다음 정류장 가져옴
                BusStop nextStopInfo = routeDataService.getStopInfo().get(nextStopId); //다음 정류장의 정보

                //현재 정류장 - 다음 정류장 사이의 거리
                double distance = calculateDistance.getDistance(
                        current.currentStopInfo.getGpsY(), current.currentStopInfo.getGpsX(),
                        nextStopInfo.getGpsY(), nextStopInfo.getGpsX()
                );
                //현재 정류장 - 다음 정류장 사이의 진행 방향
                String azmithAngle = azimuthAngle.getDiretion(
                        current.currentStopInfo.getGpsY(), current.currentStopInfo.getGpsX(),
                        nextStopInfo.getGpsY(), nextStopInfo.getGpsX()
                );

                //출발지부터 방금 찾아낸 다음 노드(nextStopInfo)까지의 총 이동 비용(거리)
                double tentativeGScore = context.gScore.get(current.currentSegment) + distance;

                PathSegment neighborSegment = new PathSegment(nextStopId, current.currentLindId, current.currentTransfer); //"다음에 이동할 수 있는 경로 후보
                if (tentativeGScore < context.gScore.getOrDefault(neighborSegment, Double.MAX_VALUE)) { //새로 찾은 경로 (tentativeGScore)와 기존에 알고 있던 경로
                    //neighborSegment 상태로 가는 새로운 최단 경로 기록
                    context.cameFrom.put(neighborSegment, current.currentSegment);
                    context.gScore.put(neighborSegment, tentativeGScore);

                    double hScore = calculateDistance.getDistance(
                            nextStopInfo.getGpsY(), nextStopInfo.getGpsX(),
                            context.representativeEndStop.getGpsY(), context.representativeEndStop.getGpsX()
                    );
                    //우선순위 큐에 새로찾은 경로 저장
                    context.openSet.add(new RouteNode(tentativeGScore + hScore, tentativeGScore, nextStopId, current.currentLindId, current.currentTransfer, distance, azmithAngle));
                }
            }
        }
    }

    private String checkEndStop(PathfindingContext context){
        log.info("목적지 도착 경로를 재구성합니다.");
        //여기서 최장거리 방위각 도출
        //RouteNode : "방위각" : 거리
        Optional<Map.Entry<String, Double>> MaxDirection =
                context.moveVector.entrySet().stream().max(Map.Entry.comparingByValue());

        String direction = MaxDirection
                .map(Map.Entry::getKey)
                .orElse(" ");
        return direction;
    }

    private void transferBus(Node current, PathfindingContext context){
        // 6. 현재 정류장에서 환승
        if (current.currentTransfer < MAX_TRANSFERS) { //최대 환승 횟수 확인
            for (String transferLineId : routeDataService.getStopToLines().getOrDefault(current.currentStopId, Collections.emptyList())) {
                if (!transferLineId.equals(current.currentLindId)) { //타고 온 버스로는 환승 X
                    // currentSegment가 gScore에 있는지 확인
                    Double currentGScore = context.gScore.get(current.currentSegment);
                    if (currentGScore == null) {
                        log.warn("gScore에서 currentSegment를 찾을 수 없습니다: {}", current.currentSegment);
                        continue;
                    }

                    double tentativeGScore = currentGScore + TRANSFER_PENALTY;
                    PathSegment neighborSegment = new PathSegment(current.currentStopId, transferLineId, current.currentTransfer + 1);

                    if (tentativeGScore < context.gScore.getOrDefault(neighborSegment, Double.MAX_VALUE)) { //환승한 경로와 비교
                        //새로운 상태 기록
                        context.cameFrom.put(neighborSegment, current.currentSegment);
                        context.gScore.put(neighborSegment, tentativeGScore);

                        double hScore = calculateDistance.getDistance(
                                current.currentStopInfo.getGpsY(), current.currentStopInfo.getGpsX(),
                                context.representativeEndStop.getGpsY(), context.representativeEndStop.getGpsX()
                        );

                        context.openSet.add(new RouteNode(tentativeGScore + hScore, tentativeGScore, current.currentStopId, transferLineId, current.currentTransfer + 1, 0.0, ""));
                    }
                }
            }
        }
    }
}