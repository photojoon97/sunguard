package com.joon.sunguard_api.domain.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavoriteStopDto {

    private String userName;

    private String stopId;

    private String stopName;

    private String stopNo;

    @Builder
    FavoriteStopDto(String userName, String stopId, String stopName, String stopNo) {
        this.userName = userName;
        this.stopId = stopId;
        this.stopName = stopName;
        this.stopNo = stopNo;
    }
}
