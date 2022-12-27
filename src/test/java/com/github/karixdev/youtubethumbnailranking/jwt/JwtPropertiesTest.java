package com.github.karixdev.youtubethumbnailranking.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
public class JwtPropertiesTest {
    @Autowired
    JwtProperties underTest;

    @Test
    void shouldLoadCorrectIssuer() {
        assertThat(underTest.getIssuer())
                .isEqualTo("youtube-thumbnail-ranking-test");
    }

    @Test
    void shouldLoadCorrectTokenExpirationHours() {
        assertThat(underTest.getTokenExpirationHours())
                .isEqualTo(1);
    }

    @Test
    void shouldCreateCorrectAlgorithm() {
        assertThat(underTest.getAlgorithm().getName())
                .isEqualTo("RS256");
    }
}
