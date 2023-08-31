package com.github.karixdev.ratingyoutubethumbnailsapi.rating;

import com.github.karixdev.ratingyoutubethumbnailsapi.game.Game;
import com.github.karixdev.ratingyoutubethumbnailsapi.rating.payload.response.RatingResponse;
import com.github.karixdev.ratingyoutubethumbnailsapi.round.RoundService;
import com.github.karixdev.ratingyoutubethumbnailsapi.security.UserPrincipal;
import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.Thumbnail;
import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.ThumbnailService;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingProperties properties;
    private final RatingRepository repository;
    private final ThumbnailService thumbnailService;

    @Transactional
    public Thumbnail pickOpponent(Game game, Thumbnail thumbnail, User user) {
        Rating thumbnailRating = repository
                .findByThumbnailAndUser(thumbnail, user)
                .orElseGet(() -> createRatingForThumbnailAndUser(thumbnail, user));

        return thumbnailService.getThumbnailNotInGameWithClosestRating(
                game,
                user,
                properties.getBasePoints(),
                thumbnail,
                thumbnailRating.getPoints()
        ).orElseThrow(() -> new RuntimeException("Could not find opponent"));
    }

    @Transactional
    public void updateRatings(Thumbnail winner, Thumbnail loser, User user) {
        Rating winnerRating = repository.findByThumbnailAndUser(winner, user)
                .orElseGet(() -> createRatingForThumbnailAndUser(winner, user));

        Rating loserRating = repository.findByThumbnailAndUser(loser, user)
                .orElseGet(() -> createRatingForThumbnailAndUser(loser, user));

        BigDecimal probOfWinnerWinning =
                probabilityOfThumbnailWinning(winnerRating, loserRating);

        BigDecimal winnerNewPoints = (new BigDecimal(properties.getKParameter()))
                .multiply(BigDecimal.ONE.subtract(probOfWinnerWinning))
                .add(winnerRating.getPoints());

        BigDecimal loserNewPoints = (new BigDecimal(properties.getKParameter()))
                .multiply(probOfWinnerWinning.subtract(BigDecimal.ONE))
                .add(loserRating.getPoints());

        winnerRating.setPoints(winnerNewPoints);
        loserRating.setPoints(loserNewPoints);

        repository.save(winnerRating);
        repository.save(loserRating);
    }

    private BigDecimal probabilityOfThumbnailWinning(Rating rating, Rating otherRating) {
        // Goal: 1 / (1 + 10^[{Ra-Rb}/400])

        // Ra - Rb
        double pointsDiff = otherRating.getPoints().subtract(rating.getPoints())
                .doubleValue();

        // (Ra - Rb) / 400
        double diffDivided = pointsDiff / 400.0;

        // 10^[(Ra - Rb) / 400]
        double powResult = Math.pow(10.0, diffDivided);

        // 1 + 10^[(Ra - Rb) / 400]
        double summed = powResult + 1.0;

        // 1 / (1 + 10^[{Ra-Rb}/400])
        double inverse = 1 / summed;

        return new BigDecimal(inverse)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private Rating createRatingForThumbnailAndUser(Thumbnail thumbnail, User user) {
        System.out.println("1-1");
        System.out.println(user);
        return repository.save(Rating.builder()
                .user(user)
                .thumbnail(thumbnail)
                .points(properties.getBasePoints())
                .build());
    }

    public RatingResponse getThumbnailAveragePoints(String youtubeVideoId, UserPrincipal userPrincipal) {
        Thumbnail thumbnail =
                thumbnailService.getThumbnailByYoutubeVideoId(youtubeVideoId);

        RatingResponse response = new RatingResponse();

        Optional<BigDecimal> globalAvg =
                repository.findAveragePointsByThumbnail(thumbnail);

        response.setGlobalRatingPoints(
                globalAvg.orElseGet(properties::getBasePoints));

        if (userPrincipal != null) {
            Optional<BigDecimal> userAvg =
                    repository.findAveragePointsByThumbnailAndUser(
                            thumbnail, userPrincipal.getUser());

            response.setUserRatingPoints(
                    userAvg.orElseGet(properties::getBasePoints));
        }

        return response;
    }
}
