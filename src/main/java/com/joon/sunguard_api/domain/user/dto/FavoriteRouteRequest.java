package com.joon.sunguard_api.domain.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class FavoriteRouteRequest {

    private String startStopId;
    private String endStopId;
}
