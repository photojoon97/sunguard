package com.joon.sunguard_api.domain.solar.dto;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JacksonXmlRootElement(localName = "response")
public class SolarResponseDto {
    @JacksonXmlProperty(localName = "header")
    private Header header;

    @JacksonXmlProperty(localName = "body")
    private Body body;

    @Getter
    @Setter
    public static class Header {
        @JacksonXmlProperty(localName = "resultCode")
        private String resultCode;

        @JacksonXmlProperty(localName = "resultMsg")
        private String resultMsg;
    }

    @Getter
    @Setter
    public static class Body {

        @JacksonXmlProperty(localName = "numOfRows")
        private int numOfRows;

        @JacksonXmlProperty(localName = "pageNo")
        private int pageNo;

        @JacksonXmlProperty(localName = "totalCount")
        private int totalCount;

        @JacksonXmlElementWrapper(localName = "items")
        @JacksonXmlProperty(localName = "item")
        private List<Item> items;
    }

    @Getter
    @Setter
    public static class Item {

        @JacksonXmlProperty(localName = "locdate")
        private String locdate;

        @JacksonXmlProperty(localName = "location")
        private String location;

        @JacksonXmlProperty(localName = "latitude")
        private String latitude;

        @JacksonXmlProperty(localName = "longitudeNum")
        private double longitudeNum;

        @JacksonXmlProperty(localName = "longitude")
        private String longitude;

        @JacksonXmlProperty(localName = "latitudeNum")
        private double latitudeNum;

        @JacksonXmlProperty(localName = "azimuth_09")
        private String azimuth09;

        @JacksonXmlProperty(localName = "altitude_09")
        private String altitude09;

        @JacksonXmlProperty(localName = "azimuth_12")
        private String azimuth12;

        @JacksonXmlProperty(localName = "altitude_12")
        private String altitude12;

        @JacksonXmlProperty(localName = "azimuth_15")
        private String azimuth15;

        @JacksonXmlProperty(localName = "altitude_15")
        private String altitude15;

        @JacksonXmlProperty(localName = "azimuth_18")
        private String azimuth18;

        @JacksonXmlProperty(localName = "altitude_18")
        private String altitude18;

        @JacksonXmlProperty(localName = "altitudeMeridian")
        private String altitudeMeridian;
    }
}
