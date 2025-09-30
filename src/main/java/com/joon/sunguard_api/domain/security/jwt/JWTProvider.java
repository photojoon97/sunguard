package com.joon.sunguard_api.domain.security.jwt;

import com.joon.sunguard_api.domain.security.dto.CustomOAuth2User;
import com.joon.sunguard_api.domain.security.dto.UserDTO;
import com.joon.sunguard_api.domain.security.service.RefreshTokenService;
import com.joon.sunguard_api.domain.security.util.CookieMangement;
import com.joon.sunguard_api.domain.security.util.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTProvider {

    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final CookieMangement cookieMangement;


    public Authentication reissueToken(HttpServletRequest request, HttpServletResponse response) {
        log.info("Start reissue access token");
        String refreshToken = cookieMangement.extractTokenFromCookie(request, "refresh-token");

        if(refreshToken == null || !refreshTokenService.validateRefreshToken(refreshToken)) {
            return null;
        }

        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        log.info("username = {}, role = {}", username, role);

        //TODO: Access Token 유효기간 설정파일로 분리
        String newAccessToken = jwtUtil.createJwt("accessToken" ,username, role, 1 * 30 * 1000L);
        response.addCookie(cookieMangement.createCookie("access-token", newAccessToken));

        UserDTO userDto = UserDTO.builder()
                .username(username)
                .role(Role.valueOf(role))
                .build();

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDto);

        return new UsernamePasswordAuthenticationToken(null, customOAuth2User);

    }
}
