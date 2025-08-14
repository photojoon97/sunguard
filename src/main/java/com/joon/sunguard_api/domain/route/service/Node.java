package com.joon.sunguard_api.domain.route.service;

import com.joon.sunguard_api.domain.busstop.entity.BusStop;
import com.joon.sunguard_api.domain.route.dto.PathSegment;
import com.joon.sunguard_api.domain.route.dto.RouteNode;
import com.joon.sunguard_api.domain.route.dto.RouteStep;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Node {
    public RouteNode routeNode;
    public String currentStopId;
    public String currentLindId;
    public int currentTransfer;
    public PathSegment currentSegment;
    public List<String > routeStops;
    public BusStop currentStopInfo;
}

