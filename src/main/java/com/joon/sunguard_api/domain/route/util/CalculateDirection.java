package com.joon.sunguard_api.domain.route.util;

public interface CalculateDirection {
    Directions getDirection(double lat1, double lon1, double lat2, double lon2);
}
