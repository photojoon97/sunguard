package com.joon.sunguard_api.domain.security.config;

import com.joon.sunguard_api.domain.security.handler.CustomLogoutHandler;
import com.joon.sunguard_api.domain.security.handler.CustomSuccessHandler;
import com.joon.sunguard_api.domain.security.jwt.JWTFilter;
import com.joon.sunguard_api.domain.security.jwt.JWTProvider;
import com.joon.sunguard_api.domain.security.jwt.JWTUtil;
import com.joon.sunguard_api.domain.security.service.CustomOAuth2UserService;
import com.joon.sunguard_api.domain.security.util.CookieMangement;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final CustomLogoutHandler customLogoutHandler;
    private final JWTUtil jwtUtil;
    private final CookieMangement cookieMangement;
    private final JWTProvider jwtProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 1. CSRF, Form Login, HTTP Basic 비활성화
        http
                .csrf(auth -> auth.disable())
                .formLogin(auth -> auth.disable())
                .httpBasic(auth -> auth.disable());

        http
                .requestCache(cache -> cache
                        .requestCache(new HttpSessionRequestCache()));

        // 2. 경로별 인가 규칙 설정
        http
                .authorizeHttpRequests(auth -> auth
                        // 공개 엔드포인트 접근 허용
                        .requestMatchers("/", "/login/*", "/logout/*").permitAll()
                        // 나머지 모든 요청은 인증 필요
                        .anyRequest().authenticated());

        // 3. OAuth2 로그인 설정
        http
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler));

        // 4. API 인증을 위한 JWT 필터 추가
        http
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);

        // 5. 로그아웃 설정
        http
                .logout(logout -> logout
                        .logoutUrl("/logout") // 기본 POST 요청으로 매핑됨
                        .addLogoutHandler(customLogoutHandler)
                        .logoutSuccessHandler((request, response, authentication) -> {
                            cookieMangement.deleteCookie(response, "access-token");
                            cookieMangement.deleteCookie(response, "refresh-token");
                            response.sendRedirect("/");
                        })
                        .invalidateHttpSession(true)
                );

        // 6. 세션 관리 정책 설정
        // API 요청은 JWT 필터가 상태 없이 처리함
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public JWTFilter jwtFilter() {
        return new JWTFilter(jwtUtil, jwtProvider, cookieMangement);
    }
}
