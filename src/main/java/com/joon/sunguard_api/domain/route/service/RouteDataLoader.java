package com.joon.sunguard_api.domain.route.service;

import com.joon.sunguard_api.domain.busstop.entity.BusStop;

import java.util.List;
import java.util.Map;

public interface RouteDataLoader {
    void loadData();
    BusStop getStopInfo(String stopId);
    String getLineInfo(String lineId);
    List<String> getStopNameToIds(String stopName);
    List<String> getStopToLines(String stopId);
    List<String> getLineToStops(String lineId);
    Map<String,List<String>> getNightlineToStops();
}
