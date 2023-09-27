package com.github.karixdev.ratingyoutubethumbnailsapi.game.service;

import com.github.karixdev.ratingyoutubethumbnailsapi.game.entity.Game;
import com.github.karixdev.ratingyoutubethumbnailsapi.rating.RatingServiceApi;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.rating.RatingDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.UserDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video.VideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.utils.TestUtils;
import com.github.karixdev.ratingyoutubethumbnailsapi.video.VideoServiceApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpponentSelectionServiceTest {

    @InjectMocks
    OpponentSelectionService underTest;

    @Mock
    RatingServiceApi ratingService;

    @Mock
    VideoServiceApi videoService;

    @Test
    void GivenUserWhoHasNotRatedAllMovies_WhenSelectFirstOpponent_ThenReturnsRandomNotRatedVideo() {
        // Given
        UserDTO user = TestUtils.createUserDTO();

        List<RatingDTO> ratings = List.of(
                TestUtils.createRating(user.id(), UUID.randomUUID(), "1")
        );
        List<UUID> videosIds = ratings.stream().map(RatingDTO::videoId).toList();

        when(ratingService.findUserRatings(eq(user)))
                .thenReturn(ratings);

        RatingDTO rating = ratings.get(0);
        VideoDTO video = TestUtils.createVideo(rating.videoId(), rating.userId(), "ytId");

        when(videoService.findRandom(eq(videosIds), eq(true)))
                .thenReturn(Optional.of(video));

        // When
        Optional<VideoDTO> result = underTest.selectFirstOpponent(user);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(video);
        verify(videoService, never()).findRandom(any(), eq(false));
    }

    @Test
    void GivenUserWhoRatedAllMovies_WhenSelectFirstOpponent_ThenReturnsRandomRatedVideo() {
        // Given
        UserDTO user = TestUtils.createUserDTO();

        List<RatingDTO> ratings = List.of(
                TestUtils.createRating(user.id(), UUID.randomUUID(), "1")
        );
        List<UUID> videosIds = ratings.stream().map(RatingDTO::videoId).toList();

        when(ratingService.findUserRatings(eq(user)))
                .thenReturn(ratings);

        when(videoService.findRandom(eq(videosIds), eq(true)))
                .thenReturn(Optional.empty());

        RatingDTO rating = ratings.get(0);
        VideoDTO video = TestUtils.createVideo(rating.videoId(), rating.userId(), "ytId");

        when(videoService.findRandom(eq(videosIds), eq(false)))
                .thenReturn(Optional.of(video));

        // When
        Optional<VideoDTO> result = underTest.selectFirstOpponent(user);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(video);
    }

    @Test
    void GivenUserWhoHasNotRatedAllMovies_WhenSelectSecondOpponent_ThenReturnsRandomNotRatedVideo() {
        // Given
        UserDTO user = TestUtils.createUserDTO();
        VideoDTO firstOpponent = TestUtils.createVideo(UUID.randomUUID(), user.id(), "ytId1");
        Game game = new Game();

        List<RatingDTO> ratings = List.of(
                TestUtils.createRating(user.id(), UUID.randomUUID(), "1")
        );
        List<UUID> videosIds = ratings.stream().map(RatingDTO::videoId).collect(Collectors.toList());
        videosIds.add(firstOpponent.id());

        when(ratingService.findUserRatings(eq(user)))
                .thenReturn(ratings);

        VideoDTO secondOpponent = TestUtils.createVideo(UUID.randomUUID(), user.id(), "ytId2");
        when(videoService.findRandom(eq(videosIds), eq(true)))
                .thenReturn(Optional.of(secondOpponent));

        // When
        Optional<VideoDTO> result = underTest.selectSecondOpponent(user, game, firstOpponent);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(secondOpponent);
        verify(ratingService, never()).findByUserAndVideoOrCreate(any(), any());
    }

    @Test
    void GivenUserWhoHasRatedAllMoviesAndGameWithEmptyRoundsSet_WhenSelectSecondOpponent_ThenReturnsVideoWithClosesRatingOtherThanFirstOpponent() {
        // Given
        UserDTO user = TestUtils.createUserDTO();
        VideoDTO firstOpponent = TestUtils.createVideo(UUID.randomUUID(), user.id(), "ytId1");
        Game game = Game.builder().build();

        RatingDTO secondOpponentRating = TestUtils.createRating(user.id(), UUID.randomUUID(), "3");
        List<RatingDTO> ratings = List.of(
                secondOpponentRating,
                TestUtils.createRating(user.id(), UUID.randomUUID(), "2"),
                TestUtils.createRating(user.id(), UUID.randomUUID(), "1")
        );
        List<UUID> videosIds = ratings.stream().map(RatingDTO::videoId).collect(Collectors.toList());
        videosIds.add(firstOpponent.id());

        when(ratingService.findUserRatings(eq(user)))
                .thenReturn(ratings);

        when(videoService.findRandom(eq(videosIds), eq(true)))
                .thenReturn(Optional.empty());


        RatingDTO firstOpponentRating = TestUtils.createRating(user.id(), firstOpponent.id(), "3");
        when(ratingService.findByUserAndVideoOrCreate(eq(user), eq(firstOpponent)))
                .thenReturn(firstOpponentRating);

        when(ratingService.findRatingWithSmallestPointsDiff(eq(firstOpponentRating), eq(ratings)))
                .thenReturn(Optional.of(secondOpponentRating));

        VideoDTO secondOpponent = TestUtils.createVideo(UUID.randomUUID(), user.id(), "ytId2");
        when(videoService.findById(eq(secondOpponentRating.videoId())))
                .thenReturn(Optional.of(secondOpponent));

        // When
        Optional<VideoDTO> result = underTest.selectSecondOpponent(user, game, firstOpponent);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(secondOpponent);
    }

    @Test
    void GivenUserWhoHasRatedAllMoviesAndGameWithNotEmptyRoundsSet_WhenSelectSecondOpponent_ThenReturnsVideoWithClosesRatingOtherThanFirstOpponent() {
        // Given
        UserDTO user = TestUtils.createUserDTO();
        VideoDTO firstOpponent = TestUtils.createVideo(UUID.randomUUID(), user.id(), "ytId1");

        RatingDTO rating1 = TestUtils.createRating(user.id(), UUID.randomUUID(), "1");
        RatingDTO rating2 = TestUtils.createRating(user.id(), UUID.randomUUID(), "2");
        RatingDTO rating3 = TestUtils.createRating(user.id(), UUID.randomUUID(), "3");

        Game game = Game.builder()
                .rounds(Set.of(
                        TestUtils.createGameRound(firstOpponent.id(), rating1.videoId()),
                        TestUtils.createGameRound(firstOpponent.id(), rating2.videoId())
                ))
                .build();

        List<RatingDTO> ratings = List.of(
                rating1,
                rating2,
                rating3
        );

        List<UUID> videosIds = ratings.stream().map(RatingDTO::videoId).collect(Collectors.toList());
        videosIds.add(firstOpponent.id());

        when(ratingService.findUserRatings(eq(user)))
                .thenReturn(ratings);

        when(videoService.findRandom(eq(videosIds), eq(true)))
                .thenReturn(Optional.empty());


        RatingDTO firstOpponentRating = TestUtils.createRating(user.id(), firstOpponent.id(), "3");
        when(ratingService.findByUserAndVideoOrCreate(eq(user), eq(firstOpponent)))
                .thenReturn(firstOpponentRating);

        List<RatingDTO> filteredRating = List.of(rating3);
        when(ratingService.findRatingWithSmallestPointsDiff(eq(firstOpponentRating), eq(filteredRating)))
                .thenReturn(Optional.of(rating3));

        VideoDTO secondOpponent = TestUtils.createVideo(UUID.randomUUID(), user.id(), "ytId2");
        when(videoService.findById(eq(rating3.videoId())))
                .thenReturn(Optional.of(secondOpponent));

        // When
        Optional<VideoDTO> result = underTest.selectSecondOpponent(user, game, firstOpponent);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(secondOpponent);
    }


}