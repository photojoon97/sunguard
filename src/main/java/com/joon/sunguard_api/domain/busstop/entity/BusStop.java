package com.joon.sunguard_api.domain.busstop.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bus_stops")
@Getter
@NoArgsConstructor
public class BusStop {
    @Id
    @Column(name = "bus_stop_id")
    private String stopId;

    @Column(name = "station_name")
    private String stopName;

    @Column(name = "bus_stop_no")
    private String stopNo;

    @Column(name = "gps_x")
    private Double gpsX;

    @Column(name = "gps_y")
    private Double gpsY;

    @Column(name = "bus_stop_type")
    private String stopType;

    public BusStop(String stopId, String stopNo, String stopName, double gpsX, double gpsY) {
        this.stopId = stopId;
        this.stopNo = stopNo;
        this.stopName = stopName;
        this.gpsX = gpsX;
        this.gpsY = gpsY;
    }
}
