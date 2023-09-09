package com.github.karixdev.ratingyoutubethumbnailsapi.youtube.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ThumbnailsResponse(
        @JsonProperty("default")
        ThumbnailResponse defaultRes,
        @JsonProperty("medium")
        ThumbnailResponse mediumRes,
        @JsonProperty("high")
        ThumbnailResponse highRes,
        @JsonProperty("standard")
        ThumbnailResponse standardRes,
        @JsonProperty("maxres")
        ThumbnailResponse maxRes
) {}
