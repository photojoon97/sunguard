package com.joon.sunguard_api.domain.user.controller;

import com.joon.sunguard_api.domain.route.service.PathfinderService;
import com.joon.sunguard_api.domain.security.dto.CustomOAuth2User;
import com.joon.sunguard_api.domain.user.dto.FavoriteStopDto;
import com.joon.sunguard_api.domain.user.service.FavoriteStopsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/stops")
@RequiredArgsConstructor
public class FavoriteStopController {

    private final FavoriteStopsService favoriteStopsService;

    @GetMapping("/favorite")
    public List<FavoriteStopDto> getFavoriteStops(@AuthenticationPrincipal CustomOAuth2User user){
        return favoriteStopsService.getFavoriteStops(user);
    }

    @PostMapping("/favorite/{stopId}")
    public FavoriteStopDto registerFavoriteStops(@PathVariable("stopId") String stopId, @AuthenticationPrincipal CustomOAuth2User user) {
        return favoriteStopsService.registerFavoriteStops(user, stopId);
    }

    @DeleteMapping("/favorite/{stopId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFavoriteStops(@PathVariable("stopId") String stopId, @AuthenticationPrincipal CustomOAuth2User user) {
        favoriteStopsService.deleteFavoriteStops(user, stopId);
    }

    //@PostMapping("/favorite/route")

}
