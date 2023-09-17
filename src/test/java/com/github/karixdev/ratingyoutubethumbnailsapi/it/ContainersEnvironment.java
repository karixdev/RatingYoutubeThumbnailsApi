package com.github.karixdev.ratingyoutubethumbnailsapi.it;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ActiveProfiles("test")
public class ContainersEnvironment {
    private static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:15.1-alpine")
                    .withDatabaseName("rating-youtube-thumbnails-test")
                    .withUsername("root")
                    .withPassword("root")
                    .withReuse(true);

    @BeforeAll
    static void beforeAll() {
        postgreSQLContainer.start();
    }

    @DynamicPropertySource
    static void overrideDatabaseConnectionProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add(
                "spring.datasource.url",
                postgreSQLContainer::getJdbcUrl);

        dynamicPropertyRegistry.add(
                "spring.datasource.username",
                postgreSQLContainer::getUsername);

        dynamicPropertyRegistry.add(
                "spring.datasource.password",
                postgreSQLContainer::getPassword);

        dynamicPropertyRegistry.add(
                "spring.datasource.driver-class-name",
                postgreSQLContainer::getDriverClassName);
    }
}
