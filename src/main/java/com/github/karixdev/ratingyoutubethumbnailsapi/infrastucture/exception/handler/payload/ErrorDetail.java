package com.github.karixdev.ratingyoutubethumbnailsapi.infrastucture.exception.handler.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ErrorDetail(
        @JsonProperty("timestamp")
        LocalDateTime timestamp,
        @JsonProperty("status")
        HttpStatus status,
        @JsonProperty("message")
        String message
) {}
