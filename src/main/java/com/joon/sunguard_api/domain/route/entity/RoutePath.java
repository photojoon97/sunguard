package com.joon.sunguard_api.domain.route.entity;

import com.joon.sunguard_api.domain.busstop.entity.BusStop;
import com.joon.sunguard_api.domain.busstop.entity.BusanBus;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "route_paths")
@Getter
public class RoutePath {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "path_id")
    private Integer pathId;

    @Column(name = "line_id", insertable = false, updatable = false)
    private String lineId;

    @Column(name = "bus_stop_id", insertable = false, updatable = false)
    private String stopId;

    @Column(name = "seq", nullable = false)
    private Integer sequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_stop_id")
    private BusStop busStop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private BusanBus busanBus;
}
