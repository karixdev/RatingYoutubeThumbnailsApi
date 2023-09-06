package com.github.karixdev.ratingyoutubethumbnailsapi.user.exception;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.AppException;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.ValidationException;
import org.springframework.http.HttpStatus;

public class UnavailableUserUsernameException extends ValidationException {
    public UnavailableUserUsernameException() {
        super("username", "Username is unavailable");
    }
}
