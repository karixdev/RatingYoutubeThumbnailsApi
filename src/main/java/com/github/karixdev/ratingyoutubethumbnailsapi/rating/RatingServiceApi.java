package com.github.karixdev.ratingyoutubethumbnailsapi.rating;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.rating.RatingDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.UserDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video.VideoDTO;

import java.util.List;
import java.util.Optional;

public interface RatingServiceApi {
    void updateRatingPoints(UserDTO user, VideoDTO winnerVideo, VideoDTO loserVideo);
    List<RatingDTO> findUserRatings(UserDTO userDTO);
    Optional<RatingDTO> findRatingWithSmallestPointsDiff(RatingDTO rating, List<RatingDTO> ratings);
    RatingDTO findByUserAndVideoOrCreate(UserDTO user, VideoDTO video);
}
