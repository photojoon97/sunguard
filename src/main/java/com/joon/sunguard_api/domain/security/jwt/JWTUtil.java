package com.joon.sunguard_api.domain.security.jwt;

import com.joon.sunguard_api.domain.security.util.Role;
import com.joon.sunguard_api.global.config.JWTConfig;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private final JWTConfig jwtConfig;
    private final SecretKey secretKey;

    public JWTUtil(JWTConfig jwtConfig){
        this.jwtConfig = jwtConfig;
        String secret = jwtConfig.getSecret();
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUsername(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username",String.class);
    }

    public String getRole(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role",String.class);
    }

    public Boolean isExpired(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String createJwt(String category, String username, String role, Long expiredMs){
        return Jwts.builder()
                .claim("category", category)
                .claim("username", username)
                .claim("role", Role.valueOf(role))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public String getCategory(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    //Refresh token 발급 메서드
    //Refresh token을 보안 cookie에 저장해야 함.
    //Refresh token을 언제 발급해야할지 고민해야 함
    //onAuthenticationSuccess에서 DB에 저장된 Refresh token이 만료됐는지 검증?
    //onAuthenticationUnSuccess에서 인증 실패를 감지해서 발급?
    //  만료됐다면 Refresh token 재발급

    //Refresh token으로 새로운 access token 발급

}
