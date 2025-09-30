package com.joon.sunguard_api.domain.security.handler;

import com.joon.sunguard_api.domain.security.repository.RefreshTokenRepository;
import com.joon.sunguard_api.domain.security.util.CookieMangement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomLogoutHandler implements LogoutHandler {

    private final CookieMangement cookieMangement;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        String refreshToken = cookieMangement.extractTokenFromCookie(request, "refresh-token");

        if(refreshToken == null){
            log.warn("Refresh token : null ");
            return;
        }

        refreshTokenRepository.deleteByRefreshToken(refreshToken);
    }
}
