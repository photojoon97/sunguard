package com.joon.sunguard_api.domain.security.handler;

import com.joon.sunguard_api.domain.security.dto.CustomOAuth2User;
import com.joon.sunguard_api.domain.security.jwt.JWTUtil;
import com.joon.sunguard_api.domain.security.service.RefreshTokenService;
import com.joon.sunguard_api.domain.security.util.CookieMangement;
import com.joon.sunguard_api.global.config.JWTConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final JWTConfig jwtConfig;
    private final CookieMangement cookieMangement;
    private final RefreshTokenService refreshTokenService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //TODO: JWTProvider에서 발급하고 CustomSuccessHandler에서 전달

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String username = customOAuth2User.getUsername();
        String role = customOAuth2User.getAuthorities().iterator().next().getAuthority();

        long accessTokenExpiration = jwtConfig.getAccessTokenExpiration().toMillis();
        long refreshTokenExpiration = jwtConfig.getRefreshTokenExpiration().toMillis();

        String accessToken = jwtUtil.createJwt("accessToken",username, role, accessTokenExpiration);
        String refreshToken = jwtUtil.createJwt("refreshToken", username, role,  refreshTokenExpiration);

        refreshTokenService.deleteExistRefreshToken(customOAuth2User);
        refreshTokenService.saveToken(username, refreshToken, refreshTokenExpiration);

        // Add tokens to cookies
        //TODO: 1. AccessToken 클라이언트 메모리에 저장
        // -    2. RefreshToken HTTP Only 쿠키에 저장 & SameSite=[Strict|Lax] 쿠키
        response.addCookie(cookieMangement.createCookie("access-token", accessToken));
        response.addCookie(cookieMangement.createCookie("refresh-token", refreshToken));
/*
        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
        String originUrl = "/";

        if(savedRequest != null){
            originUrl = savedRequest.getRedirectUrl();
            log.info("originUrl = {}", originUrl);
        }

        HttpSession session = request.getSession(false); //세션 무효화
*/
        // Redirect to root
        getRedirectStrategy().sendRedirect(request, response, "/");
    }
}
