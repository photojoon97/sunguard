package com.joon.sunguard_api.domain.route.dto.solarRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SolarResponseDTO {

    @JsonProperty("locdate")
    String locdate;
    String location;

    Double latitude;
    Double longitude;

    // API 응답값이 "129.0833330" 과 같은 숫자형 문자열이므로 Double로 유지 가능
    Double latitudeNum;
    Double longitudeNum;

    // API 응답값이 "109˚ 52´" 와 같은 특수기호 포함 문자열이므로 String으로 변경
    String azimuth_09;
    String altitude_09;

    String azimuth_12;
    String altitude_12;

    String azimuth_15;
    String altitude_15;

    String azimuth_18;
    String altitude_18;

    String altitudeMeridian; // 남중고도

    @Builder
    public SolarResponseDTO(String date, String location, Double latitude, Double longitude, Double latitudeNum, Double longitudeNum, String azimuth_09, String altitude_09, String azimuth_12, String altitude_12, String azimuth_15, String altitude_15, String azimuth_18, String altitude_18, String altitudeMeridian) {
        this.locdate = date;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.latitudeNum = latitudeNum;
        this.longitudeNum = longitudeNum;
        this.azimuth_09 = azimuth_09;
        this.altitude_09 = altitude_09;
        this.azimuth_12 = azimuth_12;
        this.altitude_12 = altitude_12;
        this.azimuth_15 = azimuth_15;
        this.altitude_15 = altitude_15;
        this.azimuth_18 = azimuth_18;
        this.altitude_18 = altitude_18;
        this.altitudeMeridian = altitudeMeridian;
    }

}