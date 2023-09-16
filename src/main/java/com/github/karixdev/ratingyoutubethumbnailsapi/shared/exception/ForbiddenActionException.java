package com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenActionException extends AppException {
    public ForbiddenActionException() {
        super("Forbidden access", HttpStatus.FORBIDDEN);
    }
}
