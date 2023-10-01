package com.github.karixdev.ratingyoutubethumbnailsapi.rating.service;

import com.github.karixdev.ratingyoutubethumbnailsapi.rating.entity.Rating;
import com.github.karixdev.ratingyoutubethumbnailsapi.rating.mapper.RatingMapper;
import com.github.karixdev.ratingyoutubethumbnailsapi.rating.repository.RatingRepository;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.rating.RatingDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.UserDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video.VideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.constants.RatingConstants.BASE_POINTS;
import static com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.constants.RatingConstants.K_FACTOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @InjectMocks
    RatingService underTest;

    @Mock
    RatingRepository repository;

    @Mock
    RatingMapper mapper;

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

    @Test
    void GivenUser_WhenFindUserRating_ThenRetrievesUserRatingAndMapsThemIntoDTOs() {
        // Given
        UserDTO user = TestUtils.createUserDTO();

        List<Rating> userRatings = List.of(
                TestUtils.createRating(UUID.randomUUID(), user.id(), UUID.randomUUID(), new BigDecimal("1")),
                TestUtils.createRating(UUID.randomUUID(), user.id(), UUID.randomUUID(), new BigDecimal("2"))
        );
        when(repository.findByUserId(eq(user.id())))
                .thenReturn(userRatings);

        // When
        underTest.findUserRatings(user);

        // Then
        verify(mapper).entityToDTO(eq(userRatings.get(0)));
        verify(mapper).entityToDTO(eq(userRatings.get(1)));
    }

    @Test
    void GivenRatingsListContainingOnlyRefRating_WhenFindRatingWithSmallestPointsDiff_ThenReturnsEmptyOptional() {
        // Given
        RatingDTO refRating = TestUtils.createRating("1400");
        List<RatingDTO> ratings = List.of(refRating);

        // When
        Optional<RatingDTO> result = underTest.findRatingWithSmallestPointsDiff(refRating, ratings);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void GivenRatingListAndRefRating_WhenFindRatingWithSmallestPointsDiff_ThenReturnsOptionalContainingRatingWithSmallestPointsDiff() {
        // Given
        RatingDTO refRating = TestUtils.createRating("1400");

        RatingDTO expectedRating = TestUtils.createRating("1390");
        List<RatingDTO> ratings = List.of(
                TestUtils.createRating("1450"),
                expectedRating,
                TestUtils.createRating("1700"),
                TestUtils.createRating("-100")
        );

        // When
        Optional<RatingDTO> result = underTest.findRatingWithSmallestPointsDiff(refRating, ratings);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(expectedRating);
    }

    @Test
    void GivenUserAndVideoThatNoneRatingHas_WhenFindByUserAndVideoOrCreate_ThenCreatesRatingUserAndMovieWithBaseRatingPointsAndMapsIntoDTO() {
        // Given
        UserDTO user = TestUtils.createUserDTO();
        VideoDTO video = TestUtils.createVideo(UUID.randomUUID(), user.id(), "ytId");

        when(repository.findByUserIdAndVideoId(eq(user.id()), eq(video.id())))
                .thenReturn(Optional.empty());

        Rating createdRating = TestUtils.createRating(null, user.id(), video.id(), BASE_POINTS);
        when(repository.save(eq(createdRating)))
                .thenReturn(createdRating);

        // When
        underTest.findByUserAndVideoOrCreate(user, video);

        // Then
        verify(mapper).entityToDTO(createdRating);
    }

    @Test
    void GivenRatingUserAndVideo_WhenFindByUserAndVideoOrCreate_ThenRetrievesRatingAndMapsIntoDTO() {
        // Given
        UserDTO user = TestUtils.createUserDTO();
        VideoDTO video = TestUtils.createVideo(UUID.randomUUID(), user.id(), "ytId");

        Rating rating = TestUtils.createRating(UUID.randomUUID(), user.id(), video.id(), BASE_POINTS);
        when(repository.findByUserIdAndVideoId(eq(user.id()), eq(video.id())))
                .thenReturn(Optional.of(rating));

        // When
        underTest.findByUserAndVideoOrCreate(user, video);

        // Then
        verify(mapper).entityToDTO(rating);
    }

}