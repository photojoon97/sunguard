package com.joon.sunguard_api.domain.route.service;

import com.joon.sunguard_api.domain.route.dto.RouteResponse;

public interface Pathfinder {

    RouteResponse findRoute(String startStopId, String endStopId);

}
