package com.joon.sunguard_api.global.config.datasource;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {"com.joon.sunguard_api.domain.security.repository",
                "com.joon.sunguard_api.domain.user.repository"},
        entityManagerFactoryRef = "userdbEntityManagerFactory",
        transactionManagerRef = "userdbTransactionManager"
)
public class UserDbJpaConfig {

    @Bean(name = "userdbEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean userdbEntityMangerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("userdbDataSource") DataSource dataSource) {

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "none"); //none으로 변경
        properties.put("hibernate.format_sql", "true");

        return builder
                .dataSource(dataSource)
                .packages("com.joon.sunguard_api.domain.security.entity",
                        "com.joon.sunguard_api.domain.user.entity")
                .persistenceUnit("uderdb")
                .properties(properties)
                .build();
    }

    @Bean(name = "userdbTransactionManager")
    public PlatformTransactionManager userdbTransactionManager(
            @Qualifier("userdbEntityManagerFactory") LocalContainerEntityManagerFactoryBean userdbEntityManagerFactory) {
        return new JpaTransactionManager(userdbEntityManagerFactory.getObject());
    }
}

