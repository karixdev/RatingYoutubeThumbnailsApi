package com.github.karixdev.ratingyoutubethumbnailsapi.rating.service;

import com.github.karixdev.ratingyoutubethumbnailsapi.rating.entity.Rating;
import com.github.karixdev.ratingyoutubethumbnailsapi.rating.repository.RatingRepository;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.UserDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video.VideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.constants.RatingConstants.BASE_POINTS;
import static com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.constants.RatingConstants.K_FACTOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @InjectMocks
    RatingService underTest;

    @Mock
    RatingRepository repository;

    @Test
    void GivenVideosThatDoNotHaveGivenUserRating_WhenUpdateRatingPoints_ThenNewRatingPointsAreCalculatedAndAssignedProperly() {
        // Given
        UserDTO user = TestUtils.createUserDTO();

        VideoDTO video1 = new VideoDTO(UUID.randomUUID(), user.id(), null, null);
        VideoDTO video2 = new VideoDTO(UUID.randomUUID(), user.id(), null, null);

        BigDecimal winnerRatingPoints = BASE_POINTS;
        Rating winnerRating = TestUtils.createRating(UUID.randomUUID(), user.id(), video1.id(), winnerRatingPoints);

        BigDecimal loserRatingPoints = BASE_POINTS;
        Rating loserRating = TestUtils.createRating(UUID.randomUUID(), user.id(), video2.id(), loserRatingPoints);

        when(repository.save(eq(new Rating())))
                .thenReturn(winnerRating)
                .thenReturn(loserRating);


        BigDecimal probWinnerWinning = new BigDecimal("0.5");
        BigDecimal diff = BigDecimal.ONE.subtract(probWinnerWinning).multiply(K_FACTOR);

        BigDecimal expectedNewWinnerPoints = winnerRatingPoints.add(diff);
        BigDecimal expectedNewLoserPoints = loserRatingPoints.subtract(diff);

        // When
        underTest.updateRatingPoints(user, video1, video2);

        // Then
        assertThat(winnerRating.getPoints()).isEqualByComparingTo(expectedNewWinnerPoints);
        assertThat(loserRating.getPoints()).isEqualByComparingTo(expectedNewLoserPoints);
    }

    @Test
    void GivenVideosThatHaveGivenUserRating_WhenUpdateRatingPoints_ThenNewRatingPointsAreCalculatedAndAssignedProperly() {
        // Given
        UserDTO user = TestUtils.createUserDTO();

        VideoDTO video1 = new VideoDTO(UUID.randomUUID(), user.id(), null, null);
        VideoDTO video2 = new VideoDTO(UUID.randomUUID(), user.id(), null, null);

        BigDecimal winnerRatingPoints = new BigDecimal("1000");
        Rating winnerRating = TestUtils.createRating(UUID.randomUUID(), user.id(), video1.id(), winnerRatingPoints);

        BigDecimal loserRatingPoints = new BigDecimal("1400");
        Rating loserRating = TestUtils.createRating(UUID.randomUUID(), user.id(), video2.id(), loserRatingPoints);

        when(repository.findByUserIdAndVideoId(eq(user.id()), eq(video1.id())))
                .thenReturn(Optional.of(winnerRating));

        when(repository.findByUserIdAndVideoId(eq(user.id()), eq(video2.id())))
                .thenReturn(Optional.of(loserRating));

        BigDecimal probWinnerWinning = new BigDecimal("0.09");
        BigDecimal diff = BigDecimal.ONE.subtract(probWinnerWinning).multiply(K_FACTOR);

        BigDecimal expectedNewWinnerPoints = winnerRatingPoints.add(diff);
        BigDecimal expectedNewLoserPoints = loserRatingPoints.subtract(diff);

        // When
        underTest.updateRatingPoints(user, video1, video2);

        // Then
        assertThat(winnerRating.getPoints()).isEqualByComparingTo(expectedNewWinnerPoints);
        assertThat(loserRating.getPoints()).isEqualByComparingTo(expectedNewLoserPoints);
    }

}