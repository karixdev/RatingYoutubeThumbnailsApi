package com.github.karixdev.youtubethumbnailranking.youtubeapi;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
public class YoutubeApiPropertiesIT {
    @Autowired
    YoutubeApiProperties underTest;

    @Test
    void shouldLoadBaseUrl() {
        assertThat(underTest.getBaseUrl())
                .isEqualTo("https://youtube.googleapis.com/youtube/v3");
    }

    @Test
    void shouldLoadApiKey() {
        assertThat(underTest.getApiKey())
                .isEqualTo("api-key");
    }
}
