package com.joon.sunguard_api.domain.busstop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "bus_info")
@Getter
public class BusanBus {

    @Id
    @Column(name = "line_id")
    private String lineId;

    @Column(name = "bus_line_num", nullable = false)
    private String lineNo;

    @Column(name = "bus_type", nullable = false)
    private String busType;

    @Column(name = "start_point")
    private String startPoint;

    @Column(name = "end_point")
    private String endPoint;

    @Column(name = "company_id")
    private String company;

    @Column(name = "arrival_interval") // 배차 간격
    private String arrivalInterval;

    @Column(name = "arrival_interval_norm") //배차 간격(일반)
    private String arrivalIntervalNorm;

    @Column(name = "arrival_interval_peak") //배차 간격 (출퇴근)
    private String arrivalIntervalPeak;

    @Column(name = "arrival_interval_holi") //배차 간격 출근
    private String arrivalIntervalHoli;

    @Column(name = "first_time")
    private String firstTime;

    @Column(name = "last_time")
    private String lastTime;

}
