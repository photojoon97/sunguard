package com.joon.sunguard_api.domain.route.service;

import com.joon.sunguard_api.domain.route.dto.RouteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PathfidingService {

    private final Pathfinder pathfinder;

    public RouteResponse findRoute(String startStopId, String endStopId){
        return pathfinder.findRoute(startStopId, endStopId);
    }
}
