package com.joon.sunguard_api.domain.user.service;

import com.joon.sunguard_api.domain.busstop.entity.BusStop;
import com.joon.sunguard_api.domain.busstop.repository.BusStopRepository;
import com.joon.sunguard_api.domain.security.dto.CustomOAuth2User;
import com.joon.sunguard_api.domain.security.entity.UserEntity;
import com.joon.sunguard_api.domain.security.repository.UserRepository;
import com.joon.sunguard_api.domain.user.dto.FavoriteRouteRequest;
import com.joon.sunguard_api.domain.user.dto.favoriteroute.response.FavoriteRouteResponse;
import com.joon.sunguard_api.domain.user.entity.FavoriteRoutes;
import com.joon.sunguard_api.domain.user.repository.FavoriteRouteRepository;
import com.joon.sunguard_api.global.exception.CustomException;
import com.joon.sunguard_api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional("userdbTransactionManager")
public class FavoriteRouteService {

    private final FavoriteRouteRepository favoriteRouteRepository;
    private final BusStopRepository busStopRepository;
    private final UserRepository userRepository;


    public FavoriteRouteResponse registerFavoriteRoute(CustomOAuth2User user, FavoriteRouteRequest favoriteRoute) {

        String username = user.getUsername();
        String startStopId = favoriteRoute.getStartStopId();
        String endStopId = favoriteRoute.getEndStopId();

        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        BusStop startBusStop = busStopRepository.findByStopId(startStopId).orElseThrow(() -> new CustomException(ErrorCode.BUSSTOP_NOT_FOUND));
        BusStop endBusStop = busStopRepository.findByStopId(endStopId).orElseThrow(() -> new CustomException(ErrorCode.BUSSTOP_NOT_FOUND));

        FavoriteRoutes favoriteRoutes = new FavoriteRoutes(userEntity, startBusStop, endBusStop);

        if (favoriteRouteRepository.existsByStartStopIdAndEndStopId(startBusStop.getStopId(), endBusStop.getStopId())) {
            throw new CustomException(ErrorCode.DUPLICATED_REQUEST);
        } else {
            log.info("favoriteRoutes : " + favoriteRoutes);
            favoriteRouteRepository.save(favoriteRoutes);
        }

        return FavoriteRouteResponse.builder()
                .username(username)
                .startStopId(startBusStop.getStopId())
                .startStopName(startBusStop.getStopName())
                .endStopId(endBusStop.getStopId())
                .endStopName(endBusStop.getStopName())
                .build();
    }

    public List<FavoriteRouteResponse> getFavoriteRoutes(CustomOAuth2User user) {
        String username = user.getUsername();
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<FavoriteRoutes> entity = favoriteRouteRepository.findAllByUser(userEntity);

        if(entity.isEmpty()){
            throw new CustomException(ErrorCode.ROUTE_NOT_FOUND, "등록된 즐겨찾는 경로 없음");
        }

        return  entity.stream()
                .map(route -> FavoriteRouteResponse.builder()
                        .username(route.getUser().getUsername())
                        .startStopId(route.getStartStopId())
                        .startStopName(route.getStartStopName())
                        .endStopId(route.getEndStopId())
                        .endStopName(route.getEndStopName())
                        .build()
                ).collect(Collectors.toList());
    }

    public void deleteFavoriteRoute(CustomOAuth2User user, FavoriteRouteRequest request) {

        String username = user.getUsername();
        String startStopId = request.getStartStopId();
        String endStopId = request.getEndStopId();

        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        long userId = userEntity.getId();

        try{
            favoriteRouteRepository.deleteByUserIdAndStartStopIdAndEndStopId(userId, startStopId, endStopId);
            log.info("즐겨찾기 경로 삭제 완료. userId: {}, startStopId: {}, endStopId: {}", userEntity.getId(), startStopId,endStopId);
        }catch (DataAccessException e){
            log.error("즐겨찾기 경로 삭제 실패. userId: {}, startStopId: {}, endStopId: {}", userEntity.getId(), startStopId,endStopId, e);
            throw new CustomException(ErrorCode.DATABASE_ERROR, "즐겨찾는 경로 삭제 중 오류가 발생했습니다");
        }
    }
}
