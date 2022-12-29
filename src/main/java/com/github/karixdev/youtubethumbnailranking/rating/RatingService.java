package com.github.karixdev.youtubethumbnailranking.rating;

import com.github.karixdev.youtubethumbnailranking.rating.payload.response.RatingResponse;
import com.github.karixdev.youtubethumbnailranking.security.UserPrincipal;
import com.github.karixdev.youtubethumbnailranking.thumbnail.Thumbnail;
import com.github.karixdev.youtubethumbnailranking.thumbnail.ThumbnailService;
import com.github.karixdev.youtubethumbnailranking.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.util.annotation.Nullable;

import javax.transaction.Transactional;
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

    public Thumbnail pickOpponent(Thumbnail thumbnail, User user, Thumbnail previousOpponent) {
        Rating thumbnailRating = repository
                .findByThumbnailAndUser(thumbnail, user)
                .orElseGet(() -> repository.save(Rating.builder()
                        .thumbnail(thumbnail)
                        .user(user)
                        .points(properties.getBasePoints())
                        .build()));

        List<Thumbnail> thumbnailsWithoutUserRating =
                thumbnailService.getThumbnailsWithoutUserRating(user);

        List<Rating> ratings = repository.findByUserAndThumbnailNot(
                thumbnail, user);

        // Conditional to check
        if (previousOpponent != null) {
            ratings = ratings.stream()
                    .filter(rating ->
                            !rating.getThumbnail().equals(previousOpponent))
                    .toList();

            thumbnailsWithoutUserRating = thumbnailsWithoutUserRating.stream()
                    .filter(thumbnail1 -> !thumbnail1.equals(previousOpponent))
                    .toList();
        }

        if (ratings.isEmpty()) {
            return thumbnailService.getRandomThumbnailFromList(
                    thumbnailsWithoutUserRating);
        }

        Optional<Rating> closestRatingFromListOptional = ratings.stream().min((rating1, rating2) -> {
            BigDecimal diff1 = thumbnailRating.getPoints()
                    .subtract(rating1.getPoints())
                    .abs();
            BigDecimal diff2 = thumbnailRating.getPoints()
                    .subtract(rating2.getPoints())
                    .abs();

            return diff1.compareTo(diff2);
        });

        Rating closestRatingFromList = closestRatingFromListOptional.get();

        BigDecimal diffBetweenBasePoints = thumbnailRating.getPoints()
                .subtract(properties.getBasePoints()).abs();

        // (1)
        if (closestRatingFromList.getPoints().compareTo(diffBetweenBasePoints) > 0 &&
                thumbnailsWithoutUserRating.isEmpty()) {
            return closestRatingFromList.getThumbnail();
        }

        return thumbnailService.getRandomThumbnailFromList(
                thumbnailsWithoutUserRating);
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

    @Transactional
    private Rating createRatingForThumbnailAndUser(Thumbnail thumbnail, User user) {
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
