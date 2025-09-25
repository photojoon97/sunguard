package com.joon.sunguard_api.domain.route.service;

import com.joon.sunguard_api.domain.busstop.entity.BusStop;

import java.util.List;
import java.util.Map;

public interface RouteDataLoader {
    Map<String, BusStop> getStopInfo();
    Map<String, List<String>> getStopNameToIds();
    Map<String, List<String>> getStopToLines();
    Map<String, List<String>> getLineToStops();
    Map<String, List<String>> getNightlineToStops();
    void loadData();
}
