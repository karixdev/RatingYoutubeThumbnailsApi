package com.github.karixdev.youtubethumbnailranking.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UsernameNotAvailableException extends RuntimeException {
    public UsernameNotAvailableException() {
        super("Username not available");
    }
}
