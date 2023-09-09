package com.github.karixdev.ratingyoutubethumbnailsapi.youtube.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record YoutubeApiVideosResponse(
        @JsonProperty("items")
        List<ItemResponse> items
) {}
