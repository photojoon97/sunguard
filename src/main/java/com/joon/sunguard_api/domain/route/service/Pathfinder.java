package com.joon.sunguard_api.domain.route.service;

import java.util.List;

public interface Pathfinder {

    List<Node> findRoute(String startStopId, String endStopId);

}
