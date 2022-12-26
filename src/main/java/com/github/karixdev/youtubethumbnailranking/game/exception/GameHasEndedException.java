package com.github.karixdev.youtubethumbnailranking.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class GameHasEndedException extends RuntimeException {
    public GameHasEndedException() {
        super("Game has ended");
    }
}
