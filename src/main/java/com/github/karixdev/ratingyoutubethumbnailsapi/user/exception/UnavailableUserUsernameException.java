package com.github.karixdev.ratingyoutubethumbnailsapi.user.exception;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.AppException;
import org.springframework.http.HttpStatus;

public class UnavailableUserUsernameException extends AppException {
    public UnavailableUserUsernameException() {
        super("Provided username is unavailable", HttpStatus.BAD_REQUEST);
    }
}
