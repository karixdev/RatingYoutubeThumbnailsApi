package com.github.karixdev.ratingyoutubethumbnailsapi.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidWinnerIdException extends RuntimeException {
    public InvalidWinnerIdException() {
        super("You have provided invalid winner id");
    }
}
