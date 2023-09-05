package com.github.karixdev.ratingyoutubethumbnailsapi.infrastucture.exception.handler.payload;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ErrorDetail(
        LocalDateTime timestamp,
        HttpStatus status,
        String message
) {}
