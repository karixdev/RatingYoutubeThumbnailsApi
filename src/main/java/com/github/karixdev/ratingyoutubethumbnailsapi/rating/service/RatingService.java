package com.github.karixdev.ratingyoutubethumbnailsapi.rating.service;

import com.github.karixdev.ratingyoutubethumbnailsapi.rating.RatingServiceApi;
import com.github.karixdev.ratingyoutubethumbnailsapi.rating.entity.Rating;
import com.github.karixdev.ratingyoutubethumbnailsapi.rating.mapper.RatingMapper;
import com.github.karixdev.ratingyoutubethumbnailsapi.rating.repository.RatingRepository;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.constants.RatingConstants;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.rating.RatingDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.UserDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video.VideoDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.constants.RatingConstants.BASE_POINTS;
import static com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.constants.RatingConstants.DIVIDER;
import static com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.constants.RatingConstants.POWER_BASE;

@Service
@RequiredArgsConstructor
public class RatingService implements RatingServiceApi {

    private final RatingRepository repository;
    private final RatingMapper mapper;

    @Transactional
    @Override
    public void updateRatingPoints(UserDTO user, VideoDTO winnerVideo, VideoDTO loserVideo) {
        Rating winnerRating = repository.findByUserIdAndVideoId(user.id(), winnerVideo.id()).orElse(createBaseRating(user, winnerVideo));
        Rating loserRating = repository.findByUserIdAndVideoId(user.id(), loserVideo.id()).orElse(createBaseRating(user, loserVideo));

        BigDecimal winnerPoints = winnerRating.getPoints();
        BigDecimal loserPoints = loserRating.getPoints();

        BigDecimal probOfWinnerWinning = probabilityOfWinnerWinning(winnerPoints, loserPoints);
        BigDecimal probOfLoserWinning = BigDecimal.ONE.subtract(probOfWinnerWinning);

        // Ra' = Ra + K * (1 - Ea)
        BigDecimal winnerNewPoints = winnerPoints.add(BigDecimal.ONE.subtract(probOfWinnerWinning).multiply(RatingConstants.K_FACTOR));
        winnerRating.setPoints(winnerNewPoints);

        // Rb' = Rb + K * (0 - Eb) = Rb - K * Eb
        BigDecimal loserNewPoints = loserPoints.subtract(RatingConstants.K_FACTOR.multiply(probOfLoserWinning));
        loserRating.setPoints(loserNewPoints);
    }

    @Override
    public List<RatingDTO> findUserRatings(UserDTO user) {
        return repository.findByUserId(user.id()).stream().map(mapper::entityToDTO).toList();
    }

    @Override
    public Optional<RatingDTO> findRatingWithSmallestPointsDiff(RatingDTO refRating, List<RatingDTO> ratings) {
        return ratings.stream().filter(rating -> !refRating.id().equals(rating.id())).min(pointsComparator(refRating.points()));
    }

    @Transactional
    @Override
    public RatingDTO findByUserAndVideoOrCreate(UserDTO user, VideoDTO video) {
        Rating rating = repository.findByUserIdAndVideoId(user.id(), video.id()).orElse(createBaseRating(user, video));
        return mapper.entityToDTO(rating);
    }

    private BigDecimal probabilityOfWinnerWinning(BigDecimal winner, BigDecimal loser) {
        // Ra - Rb
        BigDecimal diff = loser.subtract(winner);

        // (Ra - Rb) / 400
        BigDecimal quotient = diff.divide(DIVIDER, MathContext.DECIMAL128);

        // 10^((Ra - Rb) / 400)
        BigDecimal power = POWER_BASE.pow(quotient.intValue(), MathContext.DECIMAL128);

        // 1 + 10^((Ra - Rb) / 400)
        BigDecimal sum = BigDecimal.ONE.add(power);

        // 1 / (1 + 10^((Ra - Rb) / 400))
        BigDecimal prob = BigDecimal.ONE.divide(sum, MathContext.DECIMAL128);

        return prob.setScale(2, RoundingMode.HALF_UP);
    }

    private Rating createBaseRating(UserDTO user, VideoDTO video) {
        return repository.save(Rating.builder()
                .userId(user.id())
                .videoId(video.id())
                .points(BASE_POINTS)
                .build());
    }

    private Comparator<RatingDTO> pointsComparator(BigDecimal winnerRating) {
        return Comparator.comparing(RatingDTO::points, (points1, points2) -> comparePointsDiff(winnerRating, points1, points2));
    }

    private BigDecimal getPointsDiff(BigDecimal points1, BigDecimal points2) {
        return points1.subtract(points2).abs();
    }

    private int comparePointsDiff(BigDecimal refPoints, BigDecimal points1, BigDecimal points2) {
        BigDecimal diff1 = getPointsDiff(refPoints, points1);
        BigDecimal diff2 = getPointsDiff(refPoints, points2);

        return diff1.compareTo(diff2);
    }

}
