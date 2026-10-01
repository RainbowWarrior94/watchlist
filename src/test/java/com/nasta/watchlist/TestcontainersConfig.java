package com.nasta.watchlist;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Поднимает PostgreSQL в Docker на время тестов.
 * @ServiceConnection сам подставляет url/логин/пароль контейнера в DataSource,
 * поэтому настройки из application.yml для тестов не нужны.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfig {

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgres() {
        return new PostgreSQLContainer("postgres:17");
    }
}
