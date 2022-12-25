package com.github.karixdev.youtubethumbnailranking.rating;

import com.github.karixdev.youtubethumbnailranking.thumnail.Thumbnail;
import com.github.karixdev.youtubethumbnailranking.thumnail.ThumbnailService;
import com.github.karixdev.youtubethumbnailranking.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingProperties properties;
    private final RatingRepository repository;
    private final ThumbnailService thumbnailService;

    public Thumbnail pickOpponent(Thumbnail thumbnail, User user) {
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

        if (closestRatingFromList.getPoints().compareTo(diffBetweenBasePoints) > 0 &&
                thumbnailsWithoutUserRating.isEmpty()) {
            return closestRatingFromList.getThumbnail();
        }

        return thumbnailService.getRandomThumbnailFromList(
                thumbnailsWithoutUserRating);
    }

}
