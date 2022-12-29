package com.github.karixdev.ratingyoutubethumbnails.thumbnail.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ThumbnailAlreadyExistsException extends RuntimeException {
    public ThumbnailAlreadyExistsException() {
        super("Thumbnail with provided youtube video id already exists");
    }
}
