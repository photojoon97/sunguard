package com.joon.sunguard_api.global.config.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class MultipleDataSourceConfig {
    @Primary
    @Bean(name = "sunguardDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.sunguard")
    public DataSource sunguardDataSource() {
        return new HikariDataSource();
    }

    @Bean(name = "userdbDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.userdb")
    public DataSource userdbDataSource() {
        return new HikariDataSource();
    }
}
