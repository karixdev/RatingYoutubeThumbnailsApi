package com.github.karixdev.ratingyoutubethumbnailsapi.user.exception;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.AppException;
import org.springframework.http.HttpStatus;

public class UnavailableUserEmailException extends AppException {
    public UnavailableUserEmailException() {
        super("Provided email is unavailable", HttpStatus.BAD_REQUEST);
    }
}
