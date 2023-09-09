package com.github.karixdev.ratingyoutubethumbnailsapi.youtube.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SnippetResponse(
        @JsonProperty("title")
        String title,
        @JsonProperty("description")
        String description,
        @JsonProperty("thumbnails")
        ThumbnailsResponse thumbnails
) {}
