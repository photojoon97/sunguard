package com.joon.sunguard_api.domain.route.service;

import com.joon.sunguard_api.domain.busstop.entity.BusStop;
import com.joon.sunguard_api.domain.busstop.entity.BusanBus;
import com.joon.sunguard_api.domain.busstop.repository.BusStopRepository;
import com.joon.sunguard_api.domain.busstop.repository.BusanBusRepository;
import com.joon.sunguard_api.domain.route.entity.RoutePath;
import com.joon.sunguard_api.domain.route.repository.RoutePathRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class InMemoryRouteData implements RouteDataLoader {

    private final BusStopRepository busStopRepository;
    private final RoutePathRepository routePathRepository;
    private final BusanBusRepository busanBusRepository;

    private Map<String, BusStop> stopInfo; //정류장ID -> 정류장 정보
    private Map<String, String> lineInfo; //노선ID -> 버스 번호
    private Map<String, List<String>> lineToStops; //주간 버스의 노선 경로
    private Map<String, List<String>> nightlineToStops; //심야 버스의 노선 경로
    private Map<String, List<String>> stopToLines; //각 정류장에 오는 버스들 (주간/심야 모두 포함)
    private Map<String, List<String>> stopNameToIds; // //정류장 이름으로 ID 목록을 조회하기 위한 맵


    @Override
    @PostConstruct
    public void loadData() {
        log.info("경로 탐색 데이터 로딩 시작...");

        List<BusanBus> allBuses = busanBusRepository.findAll();

        stopInfo = busStopRepository.findAll().stream()
                .collect(Collectors.toMap(BusStop::getStopId, stop -> stop));

        lineInfo = allBuses.stream()
                .collect(Collectors.toMap(BusanBus::getLineId, BusanBus::getLineNo));

        // [추가] 정류장 이름 -> ID 목록 맵핑 생성
        stopNameToIds = new HashMap<>();
        stopInfo.values().forEach(stop ->
                stopNameToIds.computeIfAbsent(stop.getStopName(), k -> new ArrayList<>()).add(stop.getStopId())
        );
        log.info("정류장 이름->ID 맵 생성 완료. {}개의 고유 이름", stopNameToIds.size());

        // [수정] 심야 버스 lineId를 별도로 저장
        Set<String> nightLineIds = allBuses.stream()
                .filter(bus -> bus.getBusType() != null && bus.getBusType().contains("심야"))
                .map(BusanBus::getLineId)
                .collect(Collectors.toSet());
        log.info("심야 노선 {}개 확인.", nightLineIds.size());

        List<RoutePath> allRoutePaths = routePathRepository.findAll();
        allRoutePaths.sort(Comparator.comparing(RoutePath::getLineId).thenComparing(RoutePath::getSequence));

        // [수정] lineToStops, nightlineToStops 초기화
        lineToStops = new LinkedHashMap<>();
        nightlineToStops = new LinkedHashMap<>(); // 심야 노선 맵 초기화
        stopToLines = new HashMap<>();

        // [수정] 주간/심야 노선 분리하여 저장
        allRoutePaths.forEach(path -> {
            String stopId = path.getBusStop().getStopId();
            String lineId = path.getLineId();

            if (nightLineIds.contains(lineId)) {
                // 심야 버스인 경우
                nightlineToStops.computeIfAbsent(lineId, k -> new ArrayList<>()).add(stopId);
            } else {
                // 주간 버스인 경우
                lineToStops.computeIfAbsent(lineId, k -> new ArrayList<>()).add(stopId);
            }

            // stopToLines에는 모든 버스 노선을 추가
            stopToLines.computeIfAbsent(stopId, k -> new ArrayList<>()).add(lineId);
        });

        stopToLines.replaceAll((k, v) -> v.stream().distinct().collect(Collectors.toList()));

        log.info("경로 탐색 데이터 로딩 완료. 정류장: {}개, 전체 노선: {}개", stopInfo.size(), lineInfo.size());
        log.info("주간 노선: {}개, 심야 노선: {}개", lineToStops.size(), nightlineToStops.size());
    }

    @Override
    public BusStop getStopInfo(String stopId) {
        return stopInfo.get(stopId);
    }

    @Override
    public String getLineInfo(String lineId) {
        return lineInfo.get(lineId);
    }

    @Override
    public List<String> getStopNameToIds(String stopName) {
        return stopNameToIds.get(stopName);
    }

    @Override
    public List<String> getStopToLines(String stopId) {
        return stopToLines.get(stopId);
    }

    @Override
    public List<String> getLineToStops(String lineId) {
        return lineToStops.get(lineId);
    }

    @Override
    public Map<String,List<String>> getNightlineToStops() {
        return nightlineToStops;
    }
}
