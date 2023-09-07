package com.github.karixdev.ratingyoutubethumbnailsapi.infrastucture.exception.handler.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record ErrorDetails(
        @JsonProperty("timestamp")
        LocalDateTime timestamp,
        @JsonProperty("status")
        int status,
        @JsonProperty("message")
        String message
) {}
