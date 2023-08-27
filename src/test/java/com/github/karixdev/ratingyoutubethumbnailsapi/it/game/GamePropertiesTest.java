package com.github.karixdev.ratingyoutubethumbnailsapi.it.game;

import com.github.karixdev.ratingyoutubethumbnailsapi.game.GameProperties;
import com.github.karixdev.ratingyoutubethumbnailsapi.it.ContainersEnvironment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(webEnvironment = NONE)
public class GamePropertiesTest extends ContainersEnvironment {
    @Autowired
    GameProperties underTest;

    @Test
    void shouldLoadDuration() {
        assertThat(underTest.getDuration()).isEqualTo(10);
    }
}
