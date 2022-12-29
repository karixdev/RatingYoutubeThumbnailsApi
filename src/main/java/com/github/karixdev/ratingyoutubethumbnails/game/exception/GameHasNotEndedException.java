package com.github.karixdev.ratingyoutubethumbnails.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GameHasNotEndedException extends RuntimeException {
    public GameHasNotEndedException() {
        super("Game has not ended yet");
    }
}
