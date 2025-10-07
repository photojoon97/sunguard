package com.joon.sunguard_api.domain.route.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joon.sunguard_api.domain.busstop.entity.BusStop;
import com.joon.sunguard_api.domain.busstop.entity.BusanBus;
import com.joon.sunguard_api.domain.busstop.repository.BusStopRepository;
import com.joon.sunguard_api.domain.busstop.repository.BusanBusRepository;
import com.joon.sunguard_api.domain.route.entity.RoutePath;
import com.joon.sunguard_api.domain.route.repository.RoutePathRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class RedisRouteData implements RouteDataLoader {

    private final RedisTemplate<String, Object> redisTemplate;
    private final BusStopRepository busStopRepository;
    private final RoutePathRepository routePathRepository;
    private final BusanBusRepository busanBusRepository;
    private final ObjectMapper objectMapper;

    private static final String KEY_STOP_INFO = "route:stop:info";
    private static final String KEY_LINE_INFO = "route:line:info";
    private static final String KEY_STOP_NAME_TO_IDS = "route:stop:nameToIds";
    private static final String KEY_STOP_TO_LINES = "route:stop:toLines";
    private static final String KEY_LINE_TO_STOPS = "route:line:toStops";
    private static final String KEY_NIGHT_LINE_TO_STOPS = "route:nightline:toStops";

    private static final long CACHE_TTL_HOURS = 24;

    @Override
    @PostConstruct
    public void loadData() {
        log.info("Redis 경로 데이터 로딩 시작...");

        try {
            if (isCacheValid()) {
                log.info("Redis에 유효한 캐시 존재. 로딩 스킵.");
                return;
            }

            // DB 조회
            List<BusanBus> allBuses = busanBusRepository.findAll();
            List<BusStop> allStops = busStopRepository.findAll();
            List<RoutePath> allRoutePaths = routePathRepository.findAll();

            // Redis 저장
            saveStopInfo(allStops);
            saveLineInfo(allBuses);
            saveStopNameToIds(allStops);

            Set<String> nightLineIds = allBuses.stream()
                    .filter(bus -> bus.getBusType() != null && bus.getBusType().contains("심야"))
                    .map(BusanBus::getLineId)
                    .collect(Collectors.toSet());

            saveRouteData(allRoutePaths, nightLineIds);
            setExpiration();

            log.info("Redis 데이터 로딩 완료. 정류장: {}개, 노선: {}개",
                    allStops.size(), allBuses.size());

        } catch (Exception e) {
            log.error("Redis 데이터 로딩 실패", e);
            throw new RuntimeException("Redis 초기화 실패", e);
        }
    }

    @Override
    public BusStop getStopInfo(String stopId) {
        try {
            // HGET 명령: Hash의 특정 필드만 조회
            Object value = redisTemplate.opsForHash().get(KEY_STOP_INFO, stopId);

            if (value == null) {
                log.warn("정류장 정보 없음: {}", stopId);
                return null;
            }

            return objectMapper.convertValue(value, BusStop.class);

        } catch (Exception e) {
            log.error("정류장 정보 조회 실패: {}", stopId, e);
            return null;
        }
    }

    @Override
    public String getLineInfo(String lineId) {
        try {
            Object value = redisTemplate.opsForHash().get(KEY_LINE_INFO, lineId);
            return value != null ? (String) value : null;
        } catch (Exception e) {
            log.error("노선 정보 조회 실패: {}", lineId, e);
            return null;
        }
    }

    @Override
    public List<String> getStopNameToIds(String stopName) {
        try {
            Object value = redisTemplate.opsForHash().get(KEY_STOP_NAME_TO_IDS, stopName);

            if (value == null) {
                return Collections.emptyList();
            }

            return objectMapper.convertValue(value, new TypeReference<List<String>>() {
            });

        } catch (Exception e) {
            log.error("정류장 이름->ID 조회 실패: {}", stopName, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> getStopToLines(String stopId) {
        try {
            Object value = redisTemplate.opsForHash().get(KEY_STOP_TO_LINES, stopId);

            if (value == null) {
                return Collections.emptyList();
            }

            return objectMapper.convertValue(value, new TypeReference<List<String>>() {
            });

        } catch (Exception e) {
            log.error("정류장->노선 조회 실패: {}", stopId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> getLineToStops(String lineId) {
        try {
            // 주간 버스에서 먼저 조회
            Object value = redisTemplate.opsForHash().get(KEY_LINE_TO_STOPS, lineId);

            // 없으면 심야 버스에서 조회
            if (value == null) {
                value = redisTemplate.opsForHash().get(KEY_NIGHT_LINE_TO_STOPS, lineId);
            }

            if (value == null) {
                return Collections.emptyList();
            }

            return objectMapper.convertValue(value, new TypeReference<List<String>>() {
            });

        } catch (Exception e) {
            log.error("노선->정류장 조회 실패: {}", lineId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, List<String>> getNightlineToStops() {
        try {
            Map<Object, Object> rawMap = redisTemplate.opsForHash()
                    .entries(KEY_NIGHT_LINE_TO_STOPS);

            return rawMap.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> (String) e.getKey(),
                            e -> objectMapper.convertValue(
                                    e.getValue(),
                                    new TypeReference<List<String>>() {
                                    }
                            )
                    ));
        } catch (Exception e) {
            log.error("심야 노선 전체 조회 실패", e);
            return new HashMap<>();
        }
    }

    private boolean isCacheValid() {
        return Boolean.TRUE.equals(redisTemplate.hasKey(KEY_STOP_INFO)) &&
                Boolean.TRUE.equals(redisTemplate.hasKey(KEY_LINE_INFO));
    }

    private void saveStopInfo(List<BusStop> stops) {
        Map<String, BusStop> stopMap = stops.stream()
                .collect(Collectors.toMap(BusStop::getStopId, stop -> stop));
        redisTemplate.opsForHash().putAll(KEY_STOP_INFO, stopMap);
        log.info("정류장 정보 {}개 저장", stops.size());
    }

    private void saveLineInfo(List<BusanBus> buses) {
        Map<String, String> lineMap = buses.stream()
                .collect(Collectors.toMap(BusanBus::getLineId, BusanBus::getLineNo));
        redisTemplate.opsForHash().putAll(KEY_LINE_INFO, lineMap);
        log.info("노선 정보 {}개 저장", buses.size());
    }

    private void saveStopNameToIds(List<BusStop> stops) {
        Map<String, List<String>> nameToIds = new HashMap<>();
        stops.forEach(stop ->
                nameToIds.computeIfAbsent(stop.getStopName(), k -> new ArrayList<>())
                        .add(stop.getStopId())
        );
        redisTemplate.opsForHash().putAll(KEY_STOP_NAME_TO_IDS, nameToIds);
        log.info("정류장 이름 매핑 {}개 저장", nameToIds.size());
    }

    private void saveRouteData(List<RoutePath> routePaths, Set<String> nightLineIds) {
        routePaths.sort(Comparator.comparing(RoutePath::getLineId)
                .thenComparing(RoutePath::getSequence));

        Map<String, List<String>> lineToStops = new LinkedHashMap<>();
        Map<String, List<String>> nightlineToStops = new LinkedHashMap<>();
        Map<String, List<String>> stopToLines = new HashMap<>();

        routePaths.forEach(path -> {
            String stopId = path.getBusStop().getStopId();
            String lineId = path.getLineId();

            if (nightLineIds.contains(lineId)) {
                nightlineToStops.computeIfAbsent(lineId, k -> new ArrayList<>()).add(stopId);
            } else {
                lineToStops.computeIfAbsent(lineId, k -> new ArrayList<>()).add(stopId);
            }

            stopToLines.computeIfAbsent(stopId, k -> new ArrayList<>()).add(lineId);
        });

        stopToLines.replaceAll((k, v) -> v.stream().distinct().collect(Collectors.toList()));

        redisTemplate.opsForHash().putAll(KEY_LINE_TO_STOPS, lineToStops);
        redisTemplate.opsForHash().putAll(KEY_NIGHT_LINE_TO_STOPS, nightlineToStops);
        redisTemplate.opsForHash().putAll(KEY_STOP_TO_LINES, stopToLines);

        log.info("경로 데이터 저장 완료. 주간: {}개, 심야: {}개",
                lineToStops.size(), nightlineToStops.size());
    }

    private void setExpiration() {
        redisTemplate.expire(KEY_STOP_INFO, CACHE_TTL_HOURS, TimeUnit.HOURS);
        redisTemplate.expire(KEY_LINE_INFO, CACHE_TTL_HOURS, TimeUnit.HOURS);
        redisTemplate.expire(KEY_STOP_NAME_TO_IDS, CACHE_TTL_HOURS, TimeUnit.HOURS);
        redisTemplate.expire(KEY_STOP_TO_LINES, CACHE_TTL_HOURS, TimeUnit.HOURS);
        redisTemplate.expire(KEY_LINE_TO_STOPS, CACHE_TTL_HOURS, TimeUnit.HOURS);
        redisTemplate.expire(KEY_NIGHT_LINE_TO_STOPS, CACHE_TTL_HOURS, TimeUnit.HOURS);
    }
}
