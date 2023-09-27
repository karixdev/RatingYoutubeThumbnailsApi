package com.github.karixdev.ratingyoutubethumbnailsapi.game.service;

import com.github.karixdev.ratingyoutubethumbnailsapi.game.entity.Game;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.entity.GameRound;
import com.github.karixdev.ratingyoutubethumbnailsapi.rating.RatingServiceApi;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.rating.RatingDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.UserDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video.VideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.video.VideoServiceApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OpponentSelectionService {

    private final RatingServiceApi ratingService;
    private final VideoServiceApi videoService;

    public Optional<VideoDTO> selectFirstOpponent(UserDTO user) {
        List<UUID> userRatedVideosIds = ratingService.findUserRatings(user).stream()
                .map(RatingDTO::videoId)
                .collect(Collectors.toList());

        Optional<VideoDTO> video = videoService.findRandom(userRatedVideosIds, true);
        if (video.isPresent()) {
            return video;
        }

        return videoService.findRandom(userRatedVideosIds, false);
    }

    public Optional<VideoDTO> selectSecondOpponent(UserDTO user, Game game, VideoDTO firstOpponent) {
        List<RatingDTO> userRatings = ratingService.findUserRatings(user);
        List<UUID> userRatedVideosIds = userRatings.stream().map(RatingDTO::videoId).collect(Collectors.toList());

        userRatedVideosIds.add(firstOpponent.id());

        Optional<VideoDTO> video = videoService.findRandom(userRatedVideosIds, true);
        if (video.isPresent()) {
            return video;
        }

        RatingDTO firstOpponentRating = ratingService.findByUserAndVideoOrCreate(user, firstOpponent);
        return selectVideoWithClosestRating(userRatings, game, firstOpponentRating);
    }

    private Optional<VideoDTO> selectVideoWithClosestRating(List<RatingDTO> userRatings, Game game, RatingDTO first) {
        List<RatingDTO> filteredRatings = userRatings.stream()
                .filter(rating -> !hasPairBeenInGame(game, first, rating))
                .toList();

        Optional<RatingDTO> selected = ratingService.findRatingWithSmallestPointsDiff(first, filteredRatings);
        if (selected.isEmpty()) {
            return Optional.empty();
        }

        return videoService.findById(selected.get().videoId());
    }

    private boolean hasPairBeenInGame(Game game, RatingDTO firstRating, RatingDTO secondRating) {
        return game.getRounds().stream().anyMatch(round -> isValidPair(round, firstRating.videoId(), secondRating.videoId()));
    }

    private boolean isValidPair(GameRound round, UUID firstVideoId, UUID secondVideoId) {
        return round.getFirstVideoId().equals(firstVideoId) && round.getSecondVideoId().equals(secondVideoId)
                || round.getFirstVideoId().equals(secondVideoId) && round.getSecondVideoId().equals(firstVideoId);
    }

}
