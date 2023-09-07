package com.github.karixdev.ratingyoutubethumbnailsapi.infrastucture.security.jwt.exception;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.AppException;
import org.springframework.http.HttpStatus;

public class InvalidJwtException extends AppException {
    public InvalidJwtException() {
        super("Authorization failed", HttpStatus.UNAUTHORIZED);
    }
}
