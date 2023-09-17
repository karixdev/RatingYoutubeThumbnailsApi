package com.github.karixdev.ratingyoutubethumbnailsapi.it.youtube;

import com.github.karixdev.ratingyoutubethumbnailsapi.it.ContainersEnvironment;
import com.github.karixdev.ratingyoutubethumbnailsapi.youtube.YoutubeApiProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(webEnvironment = NONE)
public class YoutubeApiPropertiesTest extends ContainersEnvironment {
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
