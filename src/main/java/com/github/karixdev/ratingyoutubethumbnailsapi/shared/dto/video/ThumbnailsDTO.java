package com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ThumbnailsDTO(
        @JsonProperty("defaultRes")
        String defaultRes,
        @JsonProperty("mediumRes")
        String mediumRes,
        @JsonProperty("highRes")
        String highRes,
        @JsonProperty("standardRes")
        String standardRes,
        @JsonProperty("maxRes")
        String maxRes
) {}
