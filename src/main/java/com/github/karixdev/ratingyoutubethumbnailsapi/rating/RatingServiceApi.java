package com.github.karixdev.ratingyoutubethumbnailsapi.rating;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.UserDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video.VideoDTO;

public interface RatingServiceApi {
    void updateRatingPoints(UserDTO user, VideoDTO winnerVideo, VideoDTO loserVideo);
}
