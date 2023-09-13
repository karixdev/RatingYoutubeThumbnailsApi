package com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.UUID;

public record VideoDTO(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("userId")
        UUID userId,
        @JsonProperty("youtubeId")
        String youtubeId,
        @JsonProperty("thumbnails")
        ThumbnailsDTO thumbnails
) {}
