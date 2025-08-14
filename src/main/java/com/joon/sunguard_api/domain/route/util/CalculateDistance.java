package com.joon.sunguard_api.domain.route.util;

import org.springframework.stereotype.Component;

@Component
public class CalculateDistance {

    public double getDistance(double lat1, double lon1, double lat2, double lon2){
        if((lat1 == lat2) && (lon1 == lon2)){
            return 0;
        }
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
