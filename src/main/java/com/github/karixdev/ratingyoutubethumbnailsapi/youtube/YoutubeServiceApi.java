package com.github.karixdev.ratingyoutubethumbnailsapi.youtube;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.youtube.YoutubeVideoDTO;

import java.util.Optional;

public interface YoutubeServiceApi {
    Optional<YoutubeVideoDTO> findYoutubeMovieById(String id);
}
