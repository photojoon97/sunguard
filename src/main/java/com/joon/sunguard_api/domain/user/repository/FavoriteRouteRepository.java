package com.joon.sunguard_api.domain.user.repository;


import com.joon.sunguard_api.domain.security.entity.UserEntity;
import com.joon.sunguard_api.domain.user.entity.FavoriteRoutes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface FavoriteRouteRepository extends JpaRepository<FavoriteRoutes, Long> {

    boolean existsByStartStopIdAndEndStopId(String startStopId, String endStopId);

    List<FavoriteRoutes> findAllByUser(UserEntity userEntity);

    @Modifying
    void deleteByUserIdAndStartStopIdAndEndStopId(long userId, String startStopId, String endStopId);
}
