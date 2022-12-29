package com.github.karixdev.ratingyoutubethumbnails.rating;

import com.github.karixdev.ratingyoutubethumbnails.ContainersEnvironment;
import com.github.karixdev.ratingyoutubethumbnails.thumbnail.Thumbnail;
import com.github.karixdev.ratingyoutubethumbnails.user.User;
import com.github.karixdev.ratingyoutubethumbnails.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RatingRepositoryTest extends ContainersEnvironment {
    @Autowired
    RatingRepository underTest;

    @Autowired
    TestEntityManager em;

    User user;

    Thumbnail thumbnail;

    Rating rating;

    @BeforeEach
    void setUp() {
        user = em.persist(User.builder()
                .email("abc@abc.pl")
                .username("username")
                .password("secret-password")
                .userRole(UserRole.ROLE_USER)
                .isEnabled(Boolean.TRUE)
                .build());

        thumbnail = em.persist(Thumbnail.builder()
                .url("thumbnail-url")
                .youtubeVideoId("youtube-id")
                .addedBy(user)
                .build());

        rating = em.persistAndFlush(Rating.builder()
                .points(new BigDecimal(1400))
                .user(user)
                .thumbnail(thumbnail)
                .build());
    }

    @Test
    void GivenThumbnailAndValidUser_WhenFindByThumbnailAndUser_ThenReturnsOptionalWithCorrectEntity() {
        // When
        Optional<Rating> result = underTest.findByThumbnailAndUser(
                thumbnail,
                user
        );

        // Then
        assertThat(result).isPresent();

        Rating resultRating = result.get();

        assertThat(resultRating.getUser()).isEqualTo(rating.getUser());
        assertThat(resultRating.getPoints()).isEqualTo(rating.getPoints());
        assertThat(resultRating.getThumbnail()).isEqualTo(rating.getThumbnail());
    }

    @Test
    void GivenUserAndOtherThumbnail_WhenFindByUserAndThumbnailNot_ThenReturnsCorrectList() {
        // Given
        Thumbnail otherThumbnail = em.persist(Thumbnail.builder()
                .url("thumbnail-url-2")
                .youtubeVideoId("youtube-id-2")
                .addedBy(user)
                .build());

        // When
        List<Rating> result = underTest.findByUserAndThumbnailNot(otherThumbnail, user);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(rating);
    }

    @Test
    void GivenNotExistingThumbnailWhoHasZeroRatings_WhenFindAveragePointsByThumbnail_ThenReturnsEmptyOptional() {
        // Given
        Thumbnail otherThumbnail = em.persistAndFlush(Thumbnail.builder()
                .url("thumbnail-url-2")
                .youtubeVideoId("youtube-id-2")
                .addedBy(user)
                .build());

        // When
        Optional<BigDecimal> result =
                underTest.findAveragePointsByThumbnail(otherThumbnail);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void GivenThumbnail_WhenFindAveragePointsByThumbnail_ThenReturnsOptionalWithCorrectValue() {
        User otherUser = em.persist(User.builder()
                .email("abc-2@abc.pl")
                .username("username-2")
                .password("secret-password")
                .userRole(UserRole.ROLE_USER)
                .isEnabled(Boolean.TRUE)
                .build());

        Rating otherRating = em.persistAndFlush(Rating.builder()
                .points(new BigDecimal(1600))
                .user(otherUser)
                .thumbnail(thumbnail)
                .build());

        // When
        Optional<BigDecimal> result =
                underTest.findAveragePointsByThumbnail(thumbnail);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(new BigDecimal("1500.0"));
    }

    @Test
    void GivenThumbnailAndUserWhoHasNotRatedThumbnail_WhenFindAveragePointsByThumbnailAndUser_ThenReturnsEmptyOptional() {
        // Given
        Thumbnail otherThumbnail = em.persistAndFlush(Thumbnail.builder()
                .url("thumbnail-url-2")
                .youtubeVideoId("youtube-id-2")
                .addedBy(user)
                .build());

        // When
        Optional<BigDecimal> result =
                underTest.findAveragePointsByThumbnailAndUser(
                        otherThumbnail, user);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void GivenThumbnailAndUser_WhenFindAveragePointsByThumbnailAndUser_ThenReturnsOptionalWithCorrectValue() {
        User otherUser = em.persist(User.builder()
                .email("abc-2@abc.pl")
                .username("username-2")
                .password("secret-password")
                .userRole(UserRole.ROLE_USER)
                .isEnabled(Boolean.TRUE)
                .build());

        em.persistAndFlush(Rating.builder()
                .points(new BigDecimal(1600))
                .user(otherUser)
                .thumbnail(thumbnail)
                .build());

        // When
        Optional<BigDecimal> result =
                underTest.findAveragePointsByThumbnailAndUser(thumbnail, user);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(new BigDecimal("1400.0"));
    }
}
