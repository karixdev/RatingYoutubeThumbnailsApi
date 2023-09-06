package com.github.karixdev.ratingyoutubethumbnailsapi.user.exception;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.ValidationException;

public class UnavailableUserEmailException extends ValidationException {
    public UnavailableUserEmailException() {
        super("email", "Email is unavailable");
    }
}
