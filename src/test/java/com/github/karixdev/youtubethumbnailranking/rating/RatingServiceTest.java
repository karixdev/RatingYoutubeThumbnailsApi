package com.github.karixdev.youtubethumbnailranking.rating;

import com.github.karixdev.youtubethumbnailranking.rating.payload.response.RatingResponse;
import com.github.karixdev.youtubethumbnailranking.security.UserPrincipal;
import com.github.karixdev.youtubethumbnailranking.thumbnail.Thumbnail;
import com.github.karixdev.youtubethumbnailranking.thumbnail.ThumbnailService;
import com.github.karixdev.youtubethumbnailranking.user.User;
import com.github.karixdev.youtubethumbnailranking.user.UserRole;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    void GivenThumbnailAndUserThatFindByUserAndThumbnailNotIsEmpty_WhenPickOpponent_ThenReturnsRandomThumbnailFromThumbnailServiceGetRandomThumbnailFromList() {
        when(repository.findByThumbnailAndUser(any(), any()))
                .thenReturn(Optional.of(rating));

        when(repository.findByUserAndThumbnailNot(any(), any()))
                .thenReturn(List.of());

        Thumbnail otherThumbnail = Thumbnail.builder()
                .id(2L)
                .addedBy(user)
                .url("thumbnail-url-2")
                .youtubeVideoId("youtube-id-2")
                .build();

        when(thumbnailService.getThumbnailsWithoutUserRating(any()))
                .thenReturn(List.of(
                        thumbnail,
                        otherThumbnail
                ));

        when(thumbnailService.getRandomThumbnailFromList(any()))
                .thenReturn(otherThumbnail);

        // When
        Thumbnail result = underTest.pickOpponent(thumbnail, user, null);

        // Then
        assertThat(result).isEqualTo(otherThumbnail);
    }

    @Test
    void GivenThumbnailAndUserThatFindByUserAndThumbnailNotIsNotEmptyButGetThumbnailsWithoutUserRatingIsEmpty_WhenPickOpponent_ThenReturnsRandomThumbnailFromThumbnailServiceGetRandomThumbnailFromList() {
        when(repository.findByThumbnailAndUser(any(), any()))
                .thenReturn(Optional.of(rating));

        when(thumbnailService.getThumbnailsWithoutUserRating(any()))
                .thenReturn(List.of());

        when(properties.getBasePoints())
                .thenReturn(new BigDecimal(1400));

        List<Rating> ratings = new ArrayList<>();

        for (int i = 1; i < 6; i++) {
            Thumbnail otherThumbnail = Thumbnail.builder()
                    .id((long) i)
                    .addedBy(user)
                    .url("thumbnail-url-" + i)
                    .youtubeVideoId("youtube-id-" + i)
                    .build();

            Rating newRating = Rating.builder()
                    .id((long) i)
                    .user(user)
                    .thumbnail(otherThumbnail)
                    .points(new BigDecimal(1500 - i))
                    .build();

            otherThumbnail.setRatings(Set.of(newRating));

            ratings.add(newRating);
        }

        when(repository.findByUserAndThumbnailNot(any(), any()))
                .thenReturn(ratings);

        // When
        Thumbnail result = underTest.pickOpponent(thumbnail, user, null);

        // Then
        assertThat(result).isEqualTo(ratings.get(4).getThumbnail());
    }

    @Test
    void GivenThumbnailAndUserThatFindByUserAndThumbnailNotIsNotEmptyButGetThumbnailsWithoutUserRatingIsNotEmpty_WhenPickOpponent_ThenReturnsRandomThumbnailFromThumbnailServiceGetRandomThumbnailFromList() {
        when(repository.findByThumbnailAndUser(any(), any()))
                .thenReturn(Optional.of(rating));

        when(properties.getBasePoints())
                .thenReturn(new BigDecimal(1400));

        Thumbnail randomThumbnail = Thumbnail.builder()
                .id(101L)
                .addedBy(user)
                .url("thumbnail-url-101")
                .youtubeVideoId("youtube-id-101")
                .build();

        when(thumbnailService.getThumbnailsWithoutUserRating(any()))
                .thenReturn(List.of(randomThumbnail));

        when(thumbnailService.getRandomThumbnailFromList(any()))
                .thenReturn(randomThumbnail);

        List<Rating> ratings = new ArrayList<>();

        for (int i = 1; i < 6; i++) {
            Thumbnail otherThumbnail = Thumbnail.builder()
                    .id((long) i)
                    .addedBy(user)
                    .url("thumbnail-url-" + i)
                    .youtubeVideoId("youtube-id-" + i)
                    .build();

            Rating newRating = Rating.builder()
                    .id((long) i)
                    .user(user)
                    .thumbnail(otherThumbnail)
                    .points(new BigDecimal(1500 - i))
                    .build();

            otherThumbnail.setRatings(Set.of(newRating));

            ratings.add(newRating);
        }

        when(repository.findByUserAndThumbnailNot(any(), any()))
                .thenReturn(ratings);

        // When
        Thumbnail result = underTest.pickOpponent(thumbnail, user, null);

        // Then
        assertThat(result).isEqualTo(randomThumbnail);
    }

    @Test
    void GivenThumbnailAndUserAndPreviousOpponentThatFindByUserAndThumbnailNotIsNotEmptyButGetThumbnailsWithoutUserRatingIsEmpty_WhenPickOpponent_ThenReturnsRandomThumbnailFromThumbnailServiceGetRandomThumbnailFromListExcludingPreviousOpponent() {
        // Given
        List<Rating> ratings = new ArrayList<>();

        for (int i = 1; i < 6; i++) {
            Thumbnail otherThumbnail = Thumbnail.builder()
                    .id((long) i)
                    .addedBy(user)
                    .url("thumbnail-url-" + i)
                    .youtubeVideoId("youtube-id-" + i)
                    .build();

            Rating newRating = Rating.builder()
                    .id((long) i)
                    .user(user)
                    .thumbnail(otherThumbnail)
                    .points(new BigDecimal(1500 - i))
                    .build();

            otherThumbnail.setRatings(Set.of(newRating));

            ratings.add(newRating);
        }

        Thumbnail previousOpponent = ratings.get(4).getThumbnail();

        when(repository.findByUserAndThumbnailNot(any(), any()))
                .thenReturn(ratings);

        when(repository.findByThumbnailAndUser(any(), any()))
                .thenReturn(Optional.of(rating));

        when(thumbnailService.getThumbnailsWithoutUserRating(any()))
                .thenReturn(List.of());

        when(properties.getBasePoints())
                .thenReturn(new BigDecimal(1400));

        // When
        Thumbnail result = underTest.pickOpponent(thumbnail, user, previousOpponent);

        // Then
        assertThat(result).isEqualTo(ratings.get(3).getThumbnail());
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
