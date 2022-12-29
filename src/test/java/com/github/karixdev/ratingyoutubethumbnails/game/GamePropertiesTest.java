package com.github.karixdev.ratingyoutubethumbnails.game;

import com.github.karixdev.ratingyoutubethumbnails.ContainersEnvironment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class GamePropertiesTest extends ContainersEnvironment {
    @Autowired
    GameProperties underTest;

    @Test
    void shouldLoadDuration() {
        assertThat(underTest.getDuration()).isEqualTo(10);
    }
}
