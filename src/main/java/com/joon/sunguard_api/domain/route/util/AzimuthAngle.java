package com.joon.sunguard_api.domain.route.util;

import org.springframework.stereotype.Component;

@Component
public class AzimuthAngle {
    /**
     * 방위각을 8방위 또는 16방위 문자로 변환하는 헬퍼 메서드 (간단한 예시)
     * @param bearing 0-360 사이의 방위각
     * @return 방향을 나타내는 문자열 (예: "북동", "남서")
     */
    public static String getDirection(double bearing) {
        if (bearing >= 337.5 || bearing < 22.5) return "북(N)";
        if (bearing >= 22.5 && bearing < 67.5) return "북동(NE)";
        if (bearing >= 67.5 && bearing < 112.5) return "동(E)";
        if (bearing >= 112.5 && bearing < 157.5) return "남동(SE)";
        if (bearing >= 157.5 && bearing < 202.5) return "남(S)";
        if (bearing >= 202.5 && bearing < 247.5) return "남서(SW)";
        if (bearing >= 247.5 && bearing < 292.5) return "서(W)";
        if (bearing >= 292.5 && bearing < 337.5) return "북서(NW)";
        return "알 수 없음";
    }

    /**
     * 두 지점의 위도, 경도 좌표를 기반으로 방위각(bearing)을 계산합니다.
     * 방위각은 정북(North)을 0°로 하여 시계 방향으로 측정됩니다.
     *
     * @param lat1 출발지 위도 (단위: decimal degrees)
     * @param lon1 출발지 경도 (단위: decimal degrees)
     * @param lat2 목적지 위도 (단위: decimal degrees)
     * @param lon2 목적지 경도 (단위: decimal degrees)
     * @return 0-360 사이의 방위각 (단위: degrees)
     */
    public static String getDiretion(double lat1, double lon1, double lat2, double lon2) {
        // 1. 계산을 위해 모든 각도를 라디안(radian)으로 변환합니다.
        // 자바의 삼각함수(Math.sin, Math.cos 등)는 라디안 값을 사용하기 때문입니다.
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // 2. 경도 차이를 계산합니다.
        double deltaLonRad = lon2Rad - lon1Rad;

        // 3. 방위각 계산 공식(Forward azimuth formula)을 적용합니다.
        // Y축 성분 계산
        double y = Math.sin(deltaLonRad) * Math.cos(lat2Rad);
        // X축 성분 계산
        double x = Math.cos(lat1Rad) * Math.sin(lat2Rad) -
                Math.sin(lat1Rad) * Math.cos(lat2Rad) * Math.cos(deltaLonRad);

        // 4. atan2 함수를 사용하여 라디안 단위의 초기 방위각을 구합니다.
        // atan2(y, x)는 x, y 좌표를 기반으로 각도를 계산해주어, 올바른 사분면의 각도를 얻을 수 있습니다.
        double initialBearingRad = Math.atan2(y, x);

        // 5. 계산된 라디안 값을 다시 도(degree) 단위로 변환합니다.
        double initialBearingDeg = Math.toDegrees(initialBearingRad);
        initialBearingDeg = (initialBearingDeg + 360) % 360;

        // 6. 계산된 각도를 0-360도 범위로 정규화합니다.
        // atan2의 결과는 -180° ~ 180° 범위이므로, 음수 값이 나올 수 있습니다.
        // (각도 + 360) % 360 연산을 통해 항상 양수의 방위각을 얻습니다.
        return getDirection(initialBearingDeg);
    }
}
