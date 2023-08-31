package com.github.karixdev.ratingyoutubethumbnailsapi.rating;

import com.github.karixdev.ratingyoutubethumbnailsapi.game.Game;
import com.github.karixdev.ratingyoutubethumbnailsapi.rating.payload.response.RatingResponse;
import com.github.karixdev.ratingyoutubethumbnailsapi.security.UserPrincipal;
import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.Thumbnail;
import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.ThumbnailService;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.User;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RatingServiceTest {
    @InjectMocks
    RatingService underTest;

    @Mock
    RatingProperties properties;

    @Mock
    RatingRepository repository;

    @Mock
    ThumbnailService thumbnailService;

    Thumbnail thumbnail;

    User user;

    Rating rating;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("email@email.com")
                .password("password")
                .username("username")
                .userRole(UserRole.ROLE_USER)
                .isEnabled(Boolean.TRUE)
                .build();

        rating = Rating.builder()
                .id(100L)
                .user(user)
                .points(new BigDecimal(1400))
                .build();

        thumbnail = Thumbnail.builder()
                .id(1L)
                .addedBy(user)
                .url("thumbnail-url")
                .youtubeVideoId("youtube-id")
                .ratings(Set.of(rating))
                .build();

        rating.setThumbnail(thumbnail);
    }

    @Test
    void GivenWinnerAndLoserAndUser_WhenUpdateRatings_ThenUpdatesThumbnailsRatings() {
        // Given
        Thumbnail otherThumbnail = Thumbnail.builder()
                .id(2L)
                .addedBy(user)
                .url("thumbnail-url-2")
                .youtubeVideoId("youtube-id-2")
                .build();

        Rating otherRating = Rating.builder()
                .id(101L)
                .user(user)
                .points(new BigDecimal(1300))
                .thumbnail(otherThumbnail)
                .build();

        otherThumbnail.setRatings(Set.of(otherRating));

        when(repository.findByThumbnailAndUser(eq(thumbnail), eq(user)))
                .thenReturn(Optional.of(rating));

        when(repository.findByThumbnailAndUser(eq(otherThumbnail), eq(user)))
                .thenReturn(Optional.of(otherRating));

        when(properties.getKParameter()).thenReturn(32);

        // When
        underTest.updateRatings(thumbnail, otherThumbnail, user);

        // Then
        assertThat(rating.getPoints())
                .isGreaterThan(new BigDecimal(1400));
        assertThat(otherRating.getPoints())
                .isLessThan(new BigDecimal(1300));

        verify(repository).save(eq((rating)));
        verify(repository).save(eq(otherRating));
    }

    @Test
    void GivenYoutubeVideoIdAndNullUserPrincipal_WhenGetThumbnailAveragePoints_ThenReturnsCorrectRatingResponse() {
        // Given
        UserPrincipal userPrincipal = null;

        User otherUser = User.builder()
                .email("email-2@email.com")
                .password("password")
                .username("username-2")
                .userRole(UserRole.ROLE_USER)
                .isEnabled(Boolean.TRUE)
                .build();

        Thumbnail otherThumbnail = Thumbnail.builder()
                .id(2L)
                .addedBy(otherUser)
                .url("thumbnail-url-2")
                .youtubeVideoId("youtube-id-2")
                .build();

        Rating otherRating = Rating.builder()
                .id(110L)
                .user(otherUser)
                .points(new BigDecimal(1337))
                .build();

        otherThumbnail.setRatings(Set.of(otherRating));

        when(repository.findAveragePointsByThumbnail(any()))
                .thenReturn(Optional.of(new BigDecimal(1337)));

        // When
        RatingResponse result = underTest.getThumbnailAveragePoints(
                "youtube-id-2", userPrincipal);

        // Then
        assertThat(result.getGlobalRatingPoints())
                .isEqualTo(new BigDecimal(1337));
        assertThat(result.getUserRatingPoints())
                .isNull();
    }

    @Test
    void GivenYoutubeVideoIdAndUserPrincipal_WhenGetThumbnailAveragePoints_ThenReturnsCorrectRatingResponse() {
        // Given
        UserPrincipal userPrincipal = new UserPrincipal(user);

        when(repository.findAveragePointsByThumbnail(any()))
                .thenReturn(Optional.of(new BigDecimal(1400)));

        when(repository.findAveragePointsByThumbnailAndUser(any(), any()))
                .thenReturn(Optional.of(new BigDecimal(1400)));

        // When
        RatingResponse result = underTest.getThumbnailAveragePoints(
                "youtube-id", userPrincipal);

        // Then
        assertThat(result.getGlobalRatingPoints())
                .isEqualTo(new BigDecimal(1400));
        assertThat(result.getUserRatingPoints())
                .isEqualTo(new BigDecimal(1400));
    }

    @Test
    void GivenYoutubeVideoIdWithNoRatingsAndUserPrincipalWhoHasNotRatedThumbnail_WhenGetThumbnailAveragePoints_ThenReturnsCorrectRatingResponse() {
        // Given
        UserPrincipal userPrincipal = new UserPrincipal(user);

        when(repository.findAveragePointsByThumbnail(any()))
                .thenReturn(Optional.empty());

        when(repository.findAveragePointsByThumbnailAndUser(any(), any()))
                .thenReturn(Optional.empty());

        when(properties.getBasePoints())
                .thenReturn(new BigDecimal(1400));

        // When
        RatingResponse result = underTest.getThumbnailAveragePoints(
                "youtube-id", userPrincipal);

        // Then
        assertThat(result.getGlobalRatingPoints())
                .isEqualTo(new BigDecimal(1400));
        assertThat(result.getUserRatingPoints())
                .isEqualTo(new BigDecimal(1400));
    }
}
