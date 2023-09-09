package com.github.karixdev.ratingyoutubethumbnailsapi.youtube.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ThumbnailResponse(
        @JsonProperty("url")
        String url,
        @JsonProperty("width")
        Integer width,
        @JsonProperty("height")
        Integer height
) {}
