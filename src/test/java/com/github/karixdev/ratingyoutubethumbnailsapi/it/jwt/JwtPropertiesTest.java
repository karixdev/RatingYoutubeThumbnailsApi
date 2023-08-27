package com.github.karixdev.ratingyoutubethumbnailsapi.it.jwt;

import com.github.karixdev.ratingyoutubethumbnailsapi.it.ContainersEnvironment;
import com.github.karixdev.ratingyoutubethumbnailsapi.jwt.JwtProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(webEnvironment = NONE)
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
