package com.joon.sunguard_api.domain.busstop.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusStopArrivalResponseDto {


    @JsonProperty("bstopid")
    private String bstopid; // 정류소 ID

    @JsonProperty("lineno")
    private String lineNo; // 버스 번호

    @JsonProperty("nodenm")
    private String nodeName; // 정류소 이름

    @JsonProperty("lineid")
    private String lineId; // 노선 ID

    @JsonProperty("min1")
    private String remainingTime; // 첫 번째 도착 예정 버스의 남은 시간(분)

    @JsonProperty(value = "station1", defaultValue = "정보 없음")
    private String remainingStops; // 첫 번째 도착 예정 버스의 남은 정류장 수

    //@JsonProperty("lowplate1")
    //private String lowPlate1; // 첫 번째 버스 저상버스 여부

    @JsonProperty("min2")
    private String nextRemainingTime; // 두 번째 도착 예정 버스의 남은 시간(분)

    @JsonProperty("station2")
    private String nextRemainingStops; // 두 번째 도착 예정 버스의 남은 정류장 수

    //@JsonProperty("lowplate2")
    //private String lowPlate2; // 두 번째 버스 저상버스 여부
    public static BusStopArrivalResponseDto setDefaultValues(BusStopArrivalResponseDto dto) {
        String defaultValue = "정보 없음";
        if(dto.getRemainingTime() == null){
            dto.setRemainingTime(defaultValue);
        }
        if(dto.getRemainingStops() == null){
            dto.setRemainingStops(defaultValue);
        }
        if(dto.getNextRemainingTime() == null){
            dto.setNextRemainingTime(defaultValue);
        }
        if(dto.getNextRemainingStops() == null){
            dto.setNextRemainingStops(defaultValue);
        }
        return dto;
    }
}