package com.joon.sunguard_api.domain.security.service;

import com.joon.sunguard_api.domain.security.dto.CustomOAuth2User;
import com.joon.sunguard_api.domain.security.entity.RefreshToken;
import com.joon.sunguard_api.domain.security.entity.UserEntity;
import com.joon.sunguard_api.domain.security.jwt.JWTUtil;
import com.joon.sunguard_api.domain.security.repository.RefreshTokenRepository;
import com.joon.sunguard_api.domain.security.repository.UserRepository;
import com.joon.sunguard_api.global.exception.CustomException;
import com.joon.sunguard_api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;


    public boolean validateRefreshToken(String token) {
        try {
            token = token == null ? null : token.trim();

            jwtUtil.isExpired(token);

            if (!"refreshToken".equals(jwtUtil.getCategory(token))) {
                log.warn("유효하지 않은 토큰 카테고리입니다: {}", jwtUtil.getCategory(token));
                return false;
            }

            RefreshToken dbRefreshToken = refreshTokenRepository.findByRefreshToken(token);
            log.info("db refresh token = {}", dbRefreshToken);

            //TODO: 검증 로직 보완
            if (dbRefreshToken == null) {
                log.warn("DB에 존재하지 않는 Refresh Token입니다.");
                return false;
            }
            //return dbRefreshToken != null && dbRefreshToken.getRefreshToken().equals(token);
            return dbRefreshToken.getRefreshToken().equals(token);
        }
        catch (Exception e) {
            log.warn("Refresh Token 검증 중 예외 발생 (만료 또는 형식 오류): {}", e.getMessage());
            return false;
        }
    }

    public void saveToken(String username, String token, long refreshTokenExpiration) {

        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        if (user == null) {
            throw new IllegalArgumentException(("User not found :" + username));
        }

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user) // 이제 user는 UserEntity 타입이므로 에러 없음
                .refreshToken(token)
                .expiredAt(LocalDateTime.now().plusNanos(refreshTokenExpiration * 1_000_000))
                .build();

        refreshTokenRepository.save(refreshToken);

    }

    public void deleteExistRefreshToken(CustomOAuth2User user) {
        String userName = user.getUsername();
        UserEntity userEntity = userRepository.findByUsername(userName).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        refreshTokenRepository.deleteAllByUserId(userEntity.getId());

    }

}