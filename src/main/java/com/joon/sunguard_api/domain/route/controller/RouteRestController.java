package com.joon.sunguard_api.domain.route.controller;

import com.joon.sunguard_api.domain.route.dto.RouteResponse;
import com.joon.sunguard_api.domain.route.service.PathfinderService;
import com.joon.sunguard_api.domain.route.service.RouteDataService;
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
    private final RouteDataService routeDataService;
    private final PathfinderService pathfinderService;

    //경로 탐색
    @GetMapping("/userRoute")
    public RouteResponse findUserRoute(
            @RequestParam("departureId") String departureId,
            @RequestParam("destinationId") String destinationId) {
        return pathfinderService.findShortestPath(departureId, destinationId);
    }
}
