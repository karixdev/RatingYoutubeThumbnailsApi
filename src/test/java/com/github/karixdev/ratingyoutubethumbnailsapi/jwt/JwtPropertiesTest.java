package com.github.karixdev.ratingyoutubethumbnailsapi.jwt;

import com.github.karixdev.ratingyoutubethumbnailsapi.ContainersEnvironment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class JwtPropertiesTest extends ContainersEnvironment {
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
