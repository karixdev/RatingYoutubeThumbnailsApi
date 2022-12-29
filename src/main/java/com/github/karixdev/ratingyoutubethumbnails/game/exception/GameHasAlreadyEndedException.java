package com.github.karixdev.ratingyoutubethumbnails.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GameHasAlreadyEndedException extends RuntimeException {
    public GameHasAlreadyEndedException() {
        super("Game has already ended");
    }
}
