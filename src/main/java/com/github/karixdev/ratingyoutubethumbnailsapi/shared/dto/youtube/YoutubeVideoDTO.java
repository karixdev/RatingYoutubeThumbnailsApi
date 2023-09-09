package com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.youtube;

public record YoutubeVideoDTO(
        String id,
        String defaultResUrl,
        String mediumResUrl,
        String highResUrl,
        String standardResUrl,
        String maxResUrl
) {}
