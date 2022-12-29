package com.github.karixdev.ratingyoutubethumbnails.youtube;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
public class YoutubeApiPropertiesTest {
    @Autowired
    YoutubeApiProperties underTest;

    @Test
    void shouldLoadBaseUrl() {
        assertThat(underTest.getBaseUrl())
                .isEqualTo("http://test-youtube-api");
    }

    @Test
    void shouldLoadApiKey() {
        assertThat(underTest.getApiKey())
                .isEqualTo("api-key");
    }
}
