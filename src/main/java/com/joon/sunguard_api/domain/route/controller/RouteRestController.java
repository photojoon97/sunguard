package com.joon.sunguard_api.domain.route.controller;

import com.joon.sunguard_api.domain.route.dto.solarRequest.SolarResponseDTO;
import com.joon.sunguard_api.domain.route.serviceV2.Node;
import com.joon.sunguard_api.domain.route.serviceV2.Pathfinder;
import com.joon.sunguard_api.domain.route.util.RecommendSeat;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteRestController {
    private final Pathfinder pathfinder;
    private final RecommendSeat recommendSeat;


    @GetMapping("/userRouteV2")
    public List<Node> findUserRouteV2(
            @RequestParam("departureId") String departureId,
            @RequestParam("destinationId") String destinationId
    ){
        return pathfinder.findRoute(departureId, destinationId);
    }

    @GetMapping("/sun")
    public SolarResponseDTO getSolarInfo(){
        return recommendSeat.getSolarInfo("부산", "20250807");
    }
}
