package com.joon.sunguard_api.domain.user.entity;

import com.joon.sunguard_api.domain.busstop.entity.BusStop;
import com.joon.sunguard_api.domain.security.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "favorite_routes",
        uniqueConstraints = @UniqueConstraint(columnNames = "user_id"))
@NoArgsConstructor
public class FavoriteRoutes extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    private String startStopId;

    private String startStopName;

    private String endStopId;

    private String endStopName;

    @Builder
    public FavoriteRoutes(UserEntity user, BusStop startStop, BusStop endStop){
        this.user = user;
        this.startStopId = startStop.getStopId();
        this.startStopName = startStop.getStopName();
        this.endStopId = endStop.getStopId();
        this.endStopName = endStop.getStopName();
    }
}
