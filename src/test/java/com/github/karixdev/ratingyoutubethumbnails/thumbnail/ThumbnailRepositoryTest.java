package com.github.karixdev.ratingyoutubethumbnails.thumbnail;

import com.github.karixdev.ratingyoutubethumbnails.user.User;
import com.github.karixdev.ratingyoutubethumbnails.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ThumbnailRepositoryTest {
    @Autowired
    ThumbnailRepository underTest;

    @Autowired
    TestEntityManager em;

    Thumbnail thumbnail;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .email("abc@abc.pl")
                .username("username")
                .password("secret-password")
                .userRole(UserRole.ROLE_USER)
                .isEnabled(Boolean.TRUE)
                .build();

        em.persist(user);

        thumbnail = Thumbnail.builder()
                .url("thumbnail-url")
                .youtubeVideoId("youtube-id")
                .addedBy(user)
                .build();

        em.persistAndFlush(thumbnail);
    }

    @Test
    void GivenNotExistingThumbnailYoutubeId_WhenFindByYoutubeId_ThenReturnsEmptyOptional() {
        // Given
        String youtubeId = "i-do-not-exist";

        // When
        Optional<Thumbnail> result = underTest.findByYoutubeVideoId(youtubeId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void GivenExistingThumbnailYoutubeId_WhenFindByYoutubeId_ThenReturnsOptionalWithCorrectEntity() {
        // Given
        String youtubeId = "youtube-id";

        // When
        Optional<Thumbnail> result = underTest.findByYoutubeVideoId(youtubeId);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.get()).isEqualTo(thumbnail);
    }

    @Test
    void WhenFindAllThumbnails_ThenReturnsCorrectList() {
        // When
        List<Thumbnail> result = underTest.findAllThumbnails();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(thumbnail);
    }
}
