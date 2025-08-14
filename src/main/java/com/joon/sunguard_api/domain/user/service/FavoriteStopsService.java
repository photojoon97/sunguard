package com.joon.sunguard_api.domain.user.service;

import com.joon.sunguard_api.domain.busstop.entity.BusStop;
import com.joon.sunguard_api.domain.busstop.repository.BusStopRepository;
import com.joon.sunguard_api.domain.security.dto.CustomOAuth2User;
import com.joon.sunguard_api.domain.security.entity.UserEntity;
import com.joon.sunguard_api.domain.security.repository.UserRepository;
import com.joon.sunguard_api.domain.user.dto.FavoriteStopDto;
import com.joon.sunguard_api.domain.user.entity.FavoriteStops;
import com.joon.sunguard_api.domain.user.repository.FavoriteStopsRepository;
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
@Slf4j
@RequiredArgsConstructor
@Transactional("userdbTransactionManager")
public class FavoriteStopsService {

    private final BusStopRepository busStopRepository;
    private final UserRepository userRepository;
    private final FavoriteStopsRepository favoriteStopsRepository;

    public FavoriteStopDto registerFavoriteStops(CustomOAuth2User user, String stopId) throws CustomException {
        BusStop stopEntity = busStopRepository.findById(stopId).orElseThrow(() -> new CustomException(ErrorCode.BUSSTOP_NOT_FOUND));

        String username = user.getUsername();

        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        FavoriteStops favoriteStops = FavoriteStops.create(userEntity, stopEntity);

        Long userId = userEntity.getId();

        if (favoriteStopsRepository.existsByUserIdAndStopId(userId, stopId)) {
            throw new CustomException(ErrorCode.DUPLICATED_REQUEST);
        }

        favoriteStopsRepository.save(favoriteStops);

        return FavoriteStopDto.builder()
                .stopName(favoriteStops.getStopName())
                .stopId(favoriteStops.getStopId())
                .stopNo(favoriteStops.getStopNo())
                .userName(favoriteStops.getUser().getName())
                .build();
    }

    public List<FavoriteStopDto> getFavoriteStops(CustomOAuth2User user) {
        String username = user.getUsername();

        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<FavoriteStops> favoriteStops = favoriteStopsRepository.findByUserId(userEntity.getId()).orElseThrow(() -> new CustomException(ErrorCode.BUSSTOP_NOT_FOUND));

        return favoriteStops.stream()
                .map(entity -> FavoriteStopDto.builder()
                        .stopName(entity.getStopName())
                        .stopId(entity.getStopId())
                        .stopNo(entity.getStopNo())
                        .build())
                .collect(Collectors.toList());
    }


    public void deleteFavoriteStops(CustomOAuth2User user, String stopId) {
        String username = user.getUsername();

        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow( () -> new CustomException(ErrorCode.USER_NOT_FOUND));

        try{
            favoriteStopsRepository.deleteByUserIdAndStopId(userEntity.getId(), stopId);
            log.info("즐겨찾기 정류장 삭제 완료. userId: {}, stopId: {}", userEntity.getId(), stopId);
        }catch (DataAccessException e){
            log.error("즐겨찾기 정류장 삭제 실패. userId: {}, stopId: {}", userEntity.getId(), stopId, e);
            throw new CustomException(ErrorCode.DATABASE_ERROR, "즐겨찾는 정류장 삭제 중 오류가 발생했습니다");
        }

    }
}
