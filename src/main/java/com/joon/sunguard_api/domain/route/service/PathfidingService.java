package com.joon.sunguard_api.domain.route.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PathfidingService {

    private final Pathfinder pathfinder;

    public List<Node> findRoute(String startStopId, String endStopId){
        return pathfinder.findRoute(startStopId, endStopId);
    }
}
