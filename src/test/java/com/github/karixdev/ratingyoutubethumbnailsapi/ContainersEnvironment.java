package com.github.karixdev.ratingyoutubethumbnailsapi;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ActiveProfiles("test")
public class ContainersEnvironment {
    static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:15.1-alpine")
                    .withDatabaseName("rating-youtube-thumbnails")
                    .withUsername("root")
                    .withPassword("root")
                    .withReuse(true);

    @BeforeAll
    static void beforeAll() {
        postgresContainer.start();
    }

    @DynamicPropertySource
    static void overrideDBConnectionProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.driver-class-name", postgresContainer::getDriverClassName);
    }
}
