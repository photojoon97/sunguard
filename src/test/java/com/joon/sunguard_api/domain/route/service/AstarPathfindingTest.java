package com.joon.sunguard_api.domain.route.service;

import com.joon.sunguard_api.domain.route.util.CalculateDirection;
import com.joon.sunguard_api.domain.route.util.CalculateDistance;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.List;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AstarPathfindingTest {

    @Autowired
    @Qualifier("inMemoryRouteData")
    private RouteDataLoader inMemoryLoader;

    @Autowired
    @Qualifier("redisRouteData")
    private RouteDataLoader redisLoader;

    @Autowired
    private CalculateDistance calculateDistance;

    @Autowired
    private CalculateDirection calculateDirection;

    private AstarPathfinding inMemoryPathfinder;
    private AstarPathfinding redisPathfinder;

    private static final List<String[]> TEST_CASES = List.of(
            new String[]{"193780301", "165500201"},
            new String[]{"193780301", "179450101"},
            new String[]{"184590101", "215060202"},
            new String[]{"184590101", "199420101"},
            new String[]{"184590101", "179370301"},
            new String[]{"184590101", "198710301"},
            new String[]{"184590101", "179450101"},
            new String[]{"174390301", "505860000"},
            new String[]{"174390301", "180050101"},
            new String[]{"174390301", "180750101"}
    );

    @BeforeEach
    void setup() {
        inMemoryPathfinder = new AstarPathfinding(inMemoryLoader,
                calculateDistance, calculateDirection, null);
        redisPathfinder = new AstarPathfinding(redisLoader,
                calculateDistance, calculateDirection, null);
    }

    @Test
    @Order(1)
    void testInMemoryPerformance() {
        StopWatch stopWatch = new StopWatch("InMemory Performance");

        // Warmup
        for (int i = 0; i < 3; i++) {
            for (String[] route : TEST_CASES) {
                inMemoryPathfinder.findRoute(route[0], route[1]);
            }
        }

        // Actual measurement
        stopWatch.start("InMemory Execution");
        for (String[] route : TEST_CASES) {
            inMemoryPathfinder.findRoute(route[0], route[1]);
        }
        stopWatch.stop();

        System.out.println(stopWatch.prettyPrint());
    }

    @Test
    @Order(2)
    void testRedisPerformance() {
        StopWatch stopWatch = new StopWatch("Redis Performance");

        // Warmup
        for (int i = 0; i < 3; i++) {
            for (String[] route : TEST_CASES) {
                redisPathfinder.findRoute(route[0], route[1]);
            }
        }

        // Actual measurement
        stopWatch.start("Redis Execution");
        for (String[] route : TEST_CASES) {
            redisPathfinder.findRoute(route[0], route[1]);
        }
        stopWatch.stop();

        System.out.println(stopWatch.prettyPrint());
    }

    @Test
    void comparePerformance() {
        int iterations = 10;

        long inMemoryTotal = 0;
        long redisTotal = 0;

        System.out.println("=== Performance Comparison ===\n");

        for (int i = 0; i < iterations; i++) {
            long inMemoryTime = measureTime(inMemoryPathfinder);
            long redisTime = measureTime(redisPathfinder);

            inMemoryTotal += inMemoryTime;
            redisTotal += redisTime;

            System.out.printf("Iteration %d: InMemory=%dms, Redis=%dms%n",
                    i + 1, inMemoryTime, redisTime);
        }

        System.out.printf("%nAverage: InMemory=%dms, Redis=%dms%n",
                inMemoryTotal / iterations, redisTotal / iterations);
    }

    private long measureTime(AstarPathfinding pathfinder) {
        long start = System.currentTimeMillis();
        for (String[] route : TEST_CASES) {
            pathfinder.findRoute(route[0], route[1]);
        }
        return System.currentTimeMillis() - start;
    }

    @Test
    void testMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();

        // InMemory 메모리 측정
        System.gc();
        long beforeInMemory = runtime.totalMemory() - runtime.freeMemory();

        for (String[] route : TEST_CASES) {
            inMemoryPathfinder.findRoute(route[0], route[1]);
        }

        long afterInMemory = runtime.totalMemory() - runtime.freeMemory();
        long inMemoryUsed = (afterInMemory - beforeInMemory) / 1024 / 1024;

        // Redis 메모리 측정
        System.gc();
        long beforeRedis = runtime.totalMemory() - runtime.freeMemory();

        for (String[] route : TEST_CASES) {
            redisPathfinder.findRoute(route[0], route[1]);
        }

        long afterRedis = runtime.totalMemory() - runtime.freeMemory();
        long redisUsed = (afterRedis - beforeRedis) / 1024 / 1024;

        System.out.printf("Memory Usage: InMemory=%dMB, Redis=%dMB%n",
                inMemoryUsed, redisUsed);
    }
}