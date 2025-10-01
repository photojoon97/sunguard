package com.joon.sunguard_api.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JWTConfig {
    private String secret;
    private Duration accessTokenExpiration;
    private Duration refreshTokenExpiration;
}
