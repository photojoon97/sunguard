package com.joon.sunguard_api.domain.route.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.joon.sunguard_api.domain.route.dto.solarRequest.SolarRequestDto;
import com.joon.sunguard_api.domain.route.dto.solarRequest.SolarResponseDTO;
import com.joon.sunguard_api.global.config.SolarApi;
import com.joon.sunguard_api.global.publicapi.OpenApiCallContext;
import com.joon.sunguard_api.global.publicapi.WrapperResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
@Slf4j
public class RecommendSeat {

    private final SolarApi solarApi;
    private final OpenApiCallContext openApiCallContext;

    public SolarResponseDTO getSolarInfo(String location, String date){
        String key = solarApi.getKey();
        String url = solarApi.getUrl();

        SolarRequestDto requestDto = new SolarRequestDto(location, date);

        //TODO :태양 방위각 API 호출은 하루에 한 번만 하면 됨.
        // 불러온 방위각 데이터는 캐시에 저장
        // 서버는 api호출을 정해진 시간에 하루에 한 번 호출하도록 자동화 해야 함
        Object rawResult = openApiCallContext.excute(
                "singleResponseStrategy",
                key,
                url,
                requestDto,
                new TypeReference<WrapperResponse<SolarResponseDTO>>() {}
        );

        SolarResponseDTO responseDTO = (rawResult instanceof SolarResponseDTO) ? (SolarResponseDTO) rawResult : null;

        if (responseDTO == null) {
            log.warn("태양 정보 없음: {} date: {}", location, date);
            return null; // 또는 예외 처리
        }

        return responseDTO;
    }

    public Directions clacShadowDirection(SolarResponseDTO SolarInfo){
        double solarAzimuth = 0;
        double nextSolarAzimuth = 0;
        double nowAzimuth;

        int startHour;
        int currentHour = LocalDateTime.now().getHour();
        int currentMinute = LocalDateTime.now().getMinute();


        if(currentHour < 9 || currentHour > 18){
            //해가 없을 경우 처리
            return null;
        }

        switch ((currentHour - 9) / 3){
            case 0:
                startHour = 9;
                solarAzimuth = Double.parseDouble(SolarInfo.getAzimuth_09());
                nextSolarAzimuth = Double.parseDouble( SolarInfo.getAzimuth_12());
                break;
            case 1:
                startHour = 12;
                solarAzimuth = Double.parseDouble( SolarInfo.getAzimuth_12());
                nextSolarAzimuth = Double.parseDouble(SolarInfo.getAzimuth_15());
                break;
            case 2:
                startHour = 15;
                solarAzimuth = Double.parseDouble(SolarInfo.getAzimuth_15());
                nextSolarAzimuth = Double.parseDouble(SolarInfo.getAzimuth_18());
                break;
            case 3:
                startHour = 18;
                solarAzimuth = Double.parseDouble(SolarInfo.getAzimuth_18());
                double shadowAzimuth = (solarAzimuth + 180) % 360;
                return convertAzimuthToDirection(shadowAzimuth);

            default:
                startHour = 0;
        }

        int elapsedMinutes = (currentHour - startHour) * 60 + currentMinute; //  현재 태양 방위각 계산 (선형 보간)
        double totalAzimuthChange = nextSolarAzimuth - solarAzimuth; //3시간 동안의 총 방위각 변화량
        double azimuthPerMinute = totalAzimuthChange / 180.0; // 분당 방위각 변화량
        double currentAzimuth = solarAzimuth + (azimuthPerMinute * elapsedMinutes); // 현재 시간의 태양 방위각 추정
        double shadowAzimuth = (currentAzimuth + 180) % 360; //그림자 방위각 계산 (태양의 정반대 방향)

        return convertAzimuthToDirection(shadowAzimuth);

    }

    private Directions convertAzimuthToDirection(double azimuth) {
        if (azimuth >= 337.5 || azimuth < 22.5) return Directions.N;
        if (azimuth >= 22.5 && azimuth < 67.5) return Directions.NE;
        if (azimuth >= 67.5 && azimuth < 112.5) return Directions.E;
        if (azimuth >= 112.5 && azimuth < 157.5) return Directions.SE;
        if (azimuth >= 157.5 && azimuth < 202.5) return Directions.S;
        if (azimuth >= 202.5 && azimuth < 247.5) return Directions.SW;
        if (azimuth >= 247.5 && azimuth < 292.5) return Directions.W;
        // if (azimuth >= 292.5 && azimuth < 337.5)
        return Directions.NW;
    }

}