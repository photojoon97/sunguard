package com.joon.sunguard_api.domain.busstop.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NearbyStopsRequest {

    private String bstopnm; //추후 검색된 정류장 근처의 정류장 조회 기능을 위해

    @NotNull(message = "위도는 필수값 입니다. ")
    private String latitude;

    @NotNull(message = "경도는 필수값 입니다. ")
    private String longitude;

    private double radius = 0.5; //설정 반경

    public NearbyStopsRequest(String latitude, String longitude, Double radius) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }
}
