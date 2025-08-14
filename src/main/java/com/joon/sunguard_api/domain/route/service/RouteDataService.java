package com.joon.sunguard_api.domain.route.service;

import com.joon.sunguard_api.domain.busstop.entity.BusStop;
import com.joon.sunguard_api.domain.busstop.entity.BusanBus;

import com.joon.sunguard_api.domain.busstop.repository.BusStopRepository;
import com.joon.sunguard_api.domain.busstop.repository.BusanBusRepository;
import com.joon.sunguard_api.domain.route.entity.RoutePath;
import com.joon.sunguard_api.domain.route.repository.RoutePathRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Getter
@RequiredArgsConstructor
@Slf4j
public class RouteDataService {
    private final BusStopRepository busStopRepository;
    private final RoutePathRepository routePathRepository;
    private final BusanBusRepository busanBusRepository;

    private Map<String, BusStop> stopInfo; //정류장ID -> 정류장 정보
    private Map<String, String> lineInfo; //노선ID -> 버스 번호
    private Map<String, List<String>> lineToStops; //버스의 노선 경로
    private Map<String, List<String>> stopToLines; //각 정류장에 오는 버스들

    //정류장 이름으로 ID 목록을 조회하기 위한 맵
    private Map<String, List<String>> stopNameToIds;

    @PostConstruct
    public void loadData() {
        log.info("경로 탐색 데이터 로딩 시작...");

        stopInfo = busStopRepository.findAll().stream()
                .collect(Collectors.toMap(BusStop::getStopId, stop -> stop));

        lineInfo = busanBusRepository.findAll().stream()
                .collect(Collectors.toMap(BusanBus::getLineId, BusanBus::getLineNo));

        // [추가] 정류장 이름 -> ID 목록 맵핑 생성
        stopNameToIds = new HashMap<>();
        stopInfo.values().forEach(stop ->
                stopNameToIds.computeIfAbsent(stop.getStopName(), k -> new ArrayList<>()).add(stop.getStopId())
        );
        log.info("정류장 이름->ID 맵 생성 완료. {}개의 고유 이름", stopNameToIds.size());

        List<RoutePath> allRoutePaths = routePathRepository.findAll();
        allRoutePaths.sort(Comparator.comparing(RoutePath::getLineId).thenComparing(RoutePath::getSequence));

        lineToStops = new LinkedHashMap<>();
        stopToLines = new HashMap<>();

        allRoutePaths.forEach(path -> {
            String stopId = path.getBusStop().getStopId();
            String lineId = path.getLineId();

            lineToStops.computeIfAbsent(lineId, k -> new ArrayList<>()).add(stopId);
            stopToLines.computeIfAbsent(stopId, k -> new ArrayList<>()).add(lineId);
        });

        stopToLines.replaceAll((k, v) -> v.stream().distinct().collect(Collectors.toList()));

        log.info("경로 탐색 데이터 로딩 완료. 정류장: {}개, 노선: {}개", stopInfo.size(), lineInfo.size());
    }
}