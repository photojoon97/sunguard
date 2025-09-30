package com.joon.sunguard_api.domain.route.controller;

import com.joon.sunguard_api.domain.route.dto.RouteResponse;
import com.joon.sunguard_api.domain.route.service.PathfidingService;
import com.joon.sunguard_api.domain.route.util.RecommendSeat;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteRestController {
    private final PathfidingService pathfidingService;
    private final RecommendSeat recommendSeat;


    @GetMapping("/userRoute")
    public RouteResponse findUserRouteV2(
            @RequestParam("departureId") String departureId,
            @RequestParam("destinationId") String destinationId
    ){
        return pathfidingService.findRoute(departureId, destinationId);
    }

    @GetMapping("/sun")
    public Double getSolarInfo(){
        return recommendSeat.getSolarInfo("부산", "20250807");
    }
}
