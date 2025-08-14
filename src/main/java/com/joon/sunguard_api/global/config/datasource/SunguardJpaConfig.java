package com.joon.sunguard_api.global.config.datasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {"com.joon.sunguard_api.domain.busstop.repository", "com.joon.sunguard_api.domain.route.repository"},
        entityManagerFactoryRef = "sunguardEntityManagerFactory",
        transactionManagerRef = "sunguardTransactionManager"
)
public class SunguardJpaConfig {

    @Primary
    @Bean(name = "sunguardEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean sunguardEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("sunguardDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.joon.sunguard_api.domain.busstop.entity", "com.joon.sunguard_api.domain.route.entity")
                .persistenceUnit("sunguard")
                .build();
    }

    @Primary
    @Bean(name = "sunguardTransactionManager")
    public PlatformTransactionManager sunguardTransactionManager(
            @Qualifier("sunguardEntityManagerFactory") LocalContainerEntityManagerFactoryBean sunguardEntityManagerFactory) {
        return new JpaTransactionManager(sunguardEntityManagerFactory.getObject());
    }
}
