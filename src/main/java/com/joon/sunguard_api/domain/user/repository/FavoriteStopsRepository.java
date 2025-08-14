package com.joon.sunguard_api.domain.user.repository;

import com.joon.sunguard_api.domain.user.entity.FavoriteStops;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface FavoriteStopsRepository extends JpaRepository<FavoriteStops, Long> {


     Optional<List<FavoriteStops>> findByUserId(Long userId);

     boolean existsByUserIdAndStopId(Long userId, String stopId);

     @Modifying
     void deleteByUserIdAndStopId(Long userId, String stopId);
}
