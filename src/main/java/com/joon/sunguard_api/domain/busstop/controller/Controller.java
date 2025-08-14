package com.joon.sunguard_api.domain.busstop.controller;

import com.joon.sunguard_api.domain.busstop.dto.request.NearbyStopsRequest;
import com.joon.sunguard_api.domain.busstop.dto.response.BusArrivalResponse;
import com.joon.sunguard_api.domain.busstop.dto.response.BusStopResponse;
import com.joon.sunguard_api.domain.busstop.service.BusstopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/busStops")
@RequiredArgsConstructor
public class Controller {
    private final BusstopService busstopService;


    //정류장 ID로 정류장 조회
    @GetMapping("/stops/{stopId}")
    public BusStopResponse findById(@PathVariable String stopId) {
        return busstopService.findBusStopByStopId(stopId);
    }

    //정류장 이름으로 버스 정류장 조회
    @GetMapping("/stops")
    public List<BusStopResponse> findByName(@RequestParam("stopName") String stopName) {
        return busstopService.findBusStopsByName(stopName);
    }

    //현재 좌표를 기준으로 근처 버스 정류장 조회
    //요청 데이터 : latitude, longitude
    @GetMapping("/stops/nearby")
    public List<BusStopResponse> searchNearbyBusStops(NearbyStopsRequest request) {
        return busstopService.searchNearbyBusStops(request);
    }

    //버스 정류장_ID를 기준으로 도착 예정 버스 조회
    @GetMapping("/stops/{stopId}/arrivals")
    public List<BusArrivalResponse> findBusArrivalsByStopId(@PathVariable String stopId) {
        return busstopService.getRealtimeArrivingBus(stopId);
    }
}
