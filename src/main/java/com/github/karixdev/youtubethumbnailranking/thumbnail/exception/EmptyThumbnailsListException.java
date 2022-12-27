package com.github.karixdev.youtubethumbnailranking.thumbnail.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class EmptyThumbnailsListException extends RuntimeException {
    public EmptyThumbnailsListException() {
        super("Provided list with thumbnails is empty");
    }
}
