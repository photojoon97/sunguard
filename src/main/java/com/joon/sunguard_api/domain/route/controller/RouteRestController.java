package com.joon.sunguard_api.domain.route.controller;

import com.joon.sunguard_api.domain.route.dto.RouteResponse;
import com.joon.sunguard_api.domain.route.dto.solarRequest.SolarResponseDTO;
import com.joon.sunguard_api.domain.route.service.PathfinderService;
import com.joon.sunguard_api.domain.route.service.RouteDataService;
import com.joon.sunguard_api.domain.route.util.RecommendSeat;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/routes")
@AllArgsConstructor
public class RouteRestController {
    private final PathfinderService pathfinderService;
    private final RecommendSeat recommendSeat;

    //경로 탐색
    @GetMapping("/userRoute")
    public RouteResponse findUserRoute(
            @RequestParam("departureId") String departureId,
            @RequestParam("destinationId") String destinationId) {
        return pathfinderService.findShortestPath(departureId, destinationId);
    }

    @GetMapping("/sun")
    public SolarResponseDTO getSolarInfo(){
        return recommendSeat.getSolarInfo("부산", "20250807");
    }
}
