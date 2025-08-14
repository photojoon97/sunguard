package com.joon.sunguard_api.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "solar-api")
@Getter
@Setter
public class SolarApi {
    private String key;
    private String url;
}
