package com.joon.sunguard_api.domain.user.dto.favoriteroute.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavoriteRouteResponse {

    private String username;
    private String startStopId;
    private String startStopName;
    private String endStopId;
    private String endStopName;

    @Builder
    public FavoriteRouteResponse(String username, String startStopId, String startStopName, String endStopId, String endStopName){
        this.username = username;
        this.startStopId = startStopId;
        this.startStopName = startStopName;
        this.endStopId = endStopId;
        this.endStopName = endStopName;
    }
}
