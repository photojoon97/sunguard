package com.joon.sunguard_api.domain.benchmark;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MemoryBenchmark {

    private final MeterRegistry meterRegistry;

    public MemoryBenchmark(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @GetMapping("/api/memory-info")
    public Map<String, Object> getMemoryInfo() {
        Map<String, Object> memoryInfo = new HashMap<>();

        // Heap 메모리
        Double heapUsed = meterRegistry.get("jvm.memory.used")
                .tag("area", "heap")
                .gauge()
                .value();

        Double heapMax = meterRegistry.get("jvm.memory.max")
                .tag("area", "heap")
                .gauge()
                .value();

        memoryInfo.put("heapUsedMB", heapUsed / 1024 / 1024);
        memoryInfo.put("heapMaxMB", heapMax / 1024 / 1024);
        memoryInfo.put("heapUsagePercent", (heapUsed / heapMax) * 100);

        return memoryInfo;
    }
}
