
// =================================================================
// 설명 : 'BusStop' 엔티티를 위한 레포지토리, 이름으로 버스 정류장을 조회하거나, 근처 버스 정류장을 조회
// =================================================================

package com.joon.sunguard_api.domain.busstop.repository;

import com.joon.sunguard_api.domain.busstop.entity.BusStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusStopRepository extends JpaRepository<BusStop, String> {


    //정류장 이름으로 DB에서 정류장 정보 조회
    List<BusStop> findByStopNameContaining(String stopName);

    Optional<BusStop> findByStopId(String stopId);

    List<BusStop> findListByStopId(String stopId);

    //현재 위치 근처 정류장 조회
    @Query("SELECT b FROM BusStop b " +
            "WHERE b.gpsY BETWEEN :minLat AND :maxLat " +
            "AND b.gpsX BETWEEN :minLon AND :maxLon")
    List<BusStop> findBusStopsInBoundingBox(
            @Param("minLat") double minLat,
            @Param("maxLat") double maxLat,
            @Param("minLon") double minLon,
            @Param("maxLon") double maxLon
    );


}

