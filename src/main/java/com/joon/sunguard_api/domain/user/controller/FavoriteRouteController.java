package com.joon.sunguard_api.domain.user.controller;

import com.joon.sunguard_api.domain.security.dto.CustomOAuth2User;
import com.joon.sunguard_api.domain.user.dto.FavoriteRouteRequest;
import com.joon.sunguard_api.domain.user.dto.favoriteroute.response.FavoriteRouteResponse;
import com.joon.sunguard_api.domain.user.service.FavoriteRouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/routes")
@RequiredArgsConstructor
@Slf4j
public class FavoriteRouteController {

    private final FavoriteRouteService favoriteRouteService;

    @PostMapping("/favorite")
    public FavoriteRouteResponse registerFavoriteRoute(@AuthenticationPrincipal CustomOAuth2User user, @RequestBody FavoriteRouteRequest request) {
        return favoriteRouteService.registerFavoriteRoute(user, request);
    }

    @GetMapping("/favorite")
    public List<FavoriteRouteResponse> getFavoriteRoutes(@AuthenticationPrincipal CustomOAuth2User user){
        return favoriteRouteService.getFavoriteRoutes(user);
    }

    @DeleteMapping("/favorite")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFavoriteRoute(@AuthenticationPrincipal CustomOAuth2User user, @RequestBody FavoriteRouteRequest request){
        favoriteRouteService.deleteFavoriteRoute(user, request);
    }
}

