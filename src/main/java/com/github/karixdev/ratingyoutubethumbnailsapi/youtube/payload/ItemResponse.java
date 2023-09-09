package com.github.karixdev.ratingyoutubethumbnailsapi.youtube.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ItemResponse(
        @JsonProperty("kind")
        String kind,
        @JsonProperty("id")
        String id,
        @JsonProperty("snippet")
        SnippetResponse snippet
) {}
