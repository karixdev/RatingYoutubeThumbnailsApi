package com.github.karixdev.ratingyoutubethumbnailsapi.auth.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponse(
        @JsonProperty("accessToken")
        String accessToken
) {}
