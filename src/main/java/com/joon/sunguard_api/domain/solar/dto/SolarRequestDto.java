package com.joon.sunguard_api.domain.solar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SolarRequestDto {

    private String location;
    private String locdate;

    SolarRequestDto(String location, String locdate){
        this.location = location;
        this.locdate = locdate;
    }
}
