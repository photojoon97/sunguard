package com.joon.sunguard_api.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "busan-bus-api")
@Getter
@Setter
public class BusanBusApi {
    private String key;
    private Url url = new Url();

    @Getter
    @Setter
    public static class Url{
        private String base_url;
        private String arrival_url;
        private String route_url;
    }
}