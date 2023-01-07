package com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.payload.response;

import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.Thumbnail;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.User;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ThumbnailResponseTest {
    @Autowired
    JacksonTester<ThumbnailResponse> jTester;

    User user;

    Thumbnail thumbnail;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("email@email.com")
                .password("password")
                .username("username")
                .userRole(UserRole.ROLE_USER)
                .isEnabled(Boolean.TRUE)
                .build();

        thumbnail = Thumbnail.builder()
                .id(1L)
                .addedBy(user)
                .url("thumbnail-url-1")
                .youtubeVideoId("youtube-id-1")
                .build();
    }

    @Test
    void testSerialize() throws IOException {
        var result = jTester.write(new ThumbnailResponse(thumbnail));

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathValue("$.id")
                .isEqualTo(1);

        assertThat(result).hasJsonPath("$.youtube_video_id");
        assertThat(result).extractingJsonPathValue("$.youtube_video_id")
                .isEqualTo("youtube-id-1");

        assertThat(result).hasJsonPath("$.added_by.username");
        assertThat(result).extractingJsonPathValue("$.added_by.username")
                .isEqualTo("username");
    }
}
