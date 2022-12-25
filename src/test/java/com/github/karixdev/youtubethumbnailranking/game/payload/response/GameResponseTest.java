package com.github.karixdev.youtubethumbnailranking.game.payload.response;

import com.github.karixdev.youtubethumbnailranking.game.Game;
import com.github.karixdev.youtubethumbnailranking.thumnail.Thumbnail;
import com.github.karixdev.youtubethumbnailranking.user.User;
import com.github.karixdev.youtubethumbnailranking.user.UserRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class GameResponseTest {
    @Autowired
    JacksonTester<GameResponse> jTester;

    User user;

    Thumbnail thumbnail1;

    Thumbnail thumbnail2;

    Game game;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("email@email.com")
                .password("password")
                .username("username")
                .userRole(UserRole.ROLE_USER)
                .isEnabled(Boolean.TRUE)
                .build();

        thumbnail1 = Thumbnail.builder()
                .id(1L)
                .addedBy(user)
                .url("thumbnail-url-1")
                .youtubeVideoId("youtube-id-1")
                .build();

        thumbnail2 = Thumbnail.builder()
                .id(2L)
                .addedBy(user)
                .url("thumbnail-url-2")
                .youtubeVideoId("youtube-id-2")
                .build();

        game = Game.builder()
                .id(1L)
                .user(user)
                .thumbnail1(thumbnail1)
                .thumbnail2(thumbnail2)
                .lastActivity(LocalDateTime.now())
                .build();
    }

    @Test
    void testSerialization() throws IOException {
        var result = jTester.write(new GameResponse(game));

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathValue("$.id")
                .isEqualTo(1);

        assertThat(result).hasJsonPath("$.thumbnail1");
        assertThat(result).hasJsonPath("$.thumbnail1.id");
        assertThat(result).extractingJsonPathValue("$.thumbnail1.id")
                .isEqualTo(1);
        assertThat(result).hasJsonPath("$.thumbnail1.url");
        assertThat(result).extractingJsonPathValue("$.thumbnail1.url")
                .isEqualTo("thumbnail-url-1");

        assertThat(result).hasJsonPath("$.thumbnail2");
        assertThat(result).hasJsonPath("$.thumbnail2.id");
        assertThat(result).extractingJsonPathValue("$.thumbnail2.id")
                .isEqualTo(2);
        assertThat(result).hasJsonPath("$.thumbnail2.url");
        assertThat(result).extractingJsonPathValue("$.thumbnail2.url")
                .isEqualTo("thumbnail-url-2");
    }

}
