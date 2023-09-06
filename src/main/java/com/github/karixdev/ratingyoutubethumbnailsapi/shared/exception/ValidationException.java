package com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ValidationException extends AppException {
    private final String field;
    private final String description;

    public ValidationException(String field, String description) {
        super("Validation error", HttpStatus.BAD_REQUEST);
        this.field = field;
        this.description = description;
    }
}
