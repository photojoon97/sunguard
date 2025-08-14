package com.joon.sunguard_api.domain.route.service;

import com.joon.sunguard_api.domain.busstop.entity.BusStop;
import com.joon.sunguard_api.domain.route.dto.PathSegment;
import com.joon.sunguard_api.domain.route.dto.RouteNode;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

@Getter
@Service
public class PathfindingContext {
    public BusStop startStop;
    public BusStop endStop;
    public String startStopName;
    public String endStopName;
    public List<String> allStartStopIds;
    public List<String> allEndStopIds;
    public BusStop representativeEndStop;

    // A* 알고리즘 상태 저장
    public PriorityQueue<RouteNode> openSet;
    public Map<PathSegment, PathSegment> cameFrom;
    public Map<PathSegment, Double> gScore;
    public Map<String, Double> moveVector;

}
