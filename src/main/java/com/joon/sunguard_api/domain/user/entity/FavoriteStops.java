package com.joon.sunguard_api.domain.user.entity;

import com.joon.sunguard_api.domain.busstop.entity.BusStop;
import com.joon.sunguard_api.domain.security.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "favorite_stops",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "stop_id"}))
@Getter
@NoArgsConstructor
public class FavoriteStops extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "stop_id", nullable = false)
    private String stopId;

    @Column
    private String stopNo;

    @Column(nullable = false)
    private String stopName;

    @Builder
    public FavoriteStops(UserEntity user, String stopId, String stopNo, String stopName) {
        this.user = user;
        this.stopId = stopId;
        this.stopNo = stopNo;
        this.stopName = stopName;
    }

    public static FavoriteStops create(UserEntity userEntity, BusStop stopEntity) {
        return new FavoriteStops(userEntity, stopEntity.getStopId(), stopEntity.getStopNo(), stopEntity.getStopName());
    }
}
