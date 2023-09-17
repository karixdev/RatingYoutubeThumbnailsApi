package com.github.karixdev.ratingyoutubethumbnailsapi.round.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmptyRoundSetException extends RuntimeException {
    public EmptyRoundSetException() {
        super("No rounds in game found");
    }
}
