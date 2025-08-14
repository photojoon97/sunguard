package com.joon.sunguard_api.domain.busstop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.joon.sunguard_api.domain.busstop.dto.response.BusStopArrivalResponseDto;
import com.joon.sunguard_api.domain.busstop.dto.request.NearbyStopsRequest;
import com.joon.sunguard_api.domain.busstop.dto.response.BusArrivalResponse;
import com.joon.sunguard_api.domain.busstop.dto.response.BusStopResponse;
import com.joon.sunguard_api.domain.busstop.entity.BusStop;
import com.joon.sunguard_api.domain.busstop.repository.BusStopRepository;
import com.joon.sunguard_api.global.config.BusanBusApi;
import com.joon.sunguard_api.global.exception.CustomException;
import com.joon.sunguard_api.global.exception.ErrorCode;
import com.joon.sunguard_api.global.publicapi.OpenApiCallContext;
import com.joon.sunguard_api.global.publicapi.WrapperResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class BusstopService {

    private final BusanBusApi busanBusApi;
    private final OpenApiCallContext openApiCallContext;
    private final BusStopRepository busStopRepository;

    //정류장 이름으로 버스 정류장 검색
    public List<BusStopResponse> findBusStopsByName(String stopName) {
        List<BusStop> busStops = busStopRepository.findByStopNameContaining(stopName);

        if (busStops.isEmpty()) {
            throw new CustomException(ErrorCode.BUSSTOP_NOT_FOUND);
        }

        return busStops.stream()
                .map(busStop -> BusStopResponse.builder()
                        .stopName(busStop.getStopName())
                        .stopId(busStop.getStopId())
                        .stopNo(busStop.getStopNo())
                        .build())
                .toList();
    }

    //현재 위치를 기준으로 근처 정류장 검색
    public List<BusStopResponse> searchNearbyBusStops(NearbyStopsRequest request) {
        double latitude = Double.parseDouble(request.getLatitude());
        double longitude = Double.parseDouble(request.getLongitude());
        double radius = request.getRadius();

        double latChange = radius / 111.0; // 위도 1도당 약 111km
        double lonChange = radius / (111.0 * Math.cos(Math.toRadians(latitude)));
        double minLat = latitude - latChange;
        double maxLat = latitude + latChange;
        double minLon = longitude - lonChange;
        double maxLon = longitude + lonChange;

        List<BusStop> candidateStops = busStopRepository.findBusStopsInBoundingBox(minLat, maxLat, minLon, maxLon);

        if(candidateStops.isEmpty()){
            throw new CustomException(ErrorCode.BUSSTOP_NOT_FOUND);
        }

        //x 좌표로 정렬 -> y 좌표로 정렬
        candidateStops.sort(Comparator.comparing(BusStop::getGpsX).thenComparing(BusStop::getGpsY));

        return candidateStops.stream()
                .map(busStop -> BusStopResponse.builder()
                        .stopName(busStop.getStopName())
                        .stopId(busStop.getStopId())
                        .stopNo(busStop.getStopNo())
                        .build()).collect(Collectors.toList());
    }

    public BusStopResponse findBusStopByStopId(String stopId) {
        BusStop busStop = busStopRepository.findById(stopId)
                .orElseThrow(() -> new CustomException(ErrorCode.BUSSTOP_NOT_FOUND));
        return BusStopResponse.builder()
                .stopId(busStop.getStopId())
                .stopName(busStop.getStopName())
                .stopNo(busStop.getStopNo())
                .build();
    }

    //실시간 버스 도착 정보 조회 서비스
    public List<BusArrivalResponse> getRealtimeArrivingBus(String busStopId) {
        String url = busanBusApi.getUrl().getArrival_url();
        String key = busanBusApi.getKey();

        // DB에서 해당 정류장에 도착하는 버스 목록 조회 (lineId, lineNo만 포함)
        List<BusStop> dbBuses = busStopRepository.findListByStopId(busStopId);

        if(dbBuses.isEmpty()){
            throw new CustomException(ErrorCode.BUS_NOT_FOUND);
        }

        // 외부 API 호출
        Object rawResult = openApiCallContext.excute(
                "listDtoStrategy",
                key,
                url,
                busStopId,
                new TypeReference<WrapperResponse<BusStopArrivalResponseDto>>() {
                }
        );

        List<BusStopArrivalResponseDto> apiBuses = (rawResult instanceof List) ? (List<BusStopArrivalResponseDto>) rawResult : List.of();

        // API 결과를 lineId를 키로 하는 Map으로 변환하여 검색 성능 향상
        Map<String, BusStopArrivalResponseDto> apiBusMap = apiBuses.stream()
                .collect(Collectors.toMap(BusStopArrivalResponseDto::getLineId, info -> info, (info1, info2) -> info1));

        // DB 조회 결과와 API 조회 결과를 조합하여 최종 응답 DTO 생성
        return dbBuses.stream()
                .map(dbBus -> {
                    BusStopArrivalResponseDto apiInfo = apiBusMap.get(dbBus.getStopId());
                    String remainingTime = "정보 없음";
                    String remainingStops = "정보 없음";

                    if (apiInfo != null) {
                        remainingTime = apiInfo.getRemainingTime() != null ? apiInfo.getRemainingTime() + "분" : "정보 없음";
                        remainingStops = apiInfo.getRemainingStops() != null ? apiInfo.getRemainingStops() + "개" : "정보 없음";
                    }

                    return BusArrivalResponse.builder()
                            .lineId(dbBus.getStopId())
                            .lineNo(dbBus.getStopNo())
                            .remainingTime(remainingTime)
                            .remainingStops(remainingStops)
                            .build();
                }).collect(Collectors.toList());
    }
}


