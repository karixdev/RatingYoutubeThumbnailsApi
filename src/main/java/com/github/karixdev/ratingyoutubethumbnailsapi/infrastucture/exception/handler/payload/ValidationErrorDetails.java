package com.github.karixdev.ratingyoutubethumbnailsapi.infrastucture.exception.handler.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationErrorDetails(
        @JsonProperty("timestamp")
        LocalDateTime timestamp,
        @JsonProperty("status")
        int status,
        @JsonProperty("message")
        String message,
        @JsonProperty("fields")
        Map<String, String> fields
) {}
