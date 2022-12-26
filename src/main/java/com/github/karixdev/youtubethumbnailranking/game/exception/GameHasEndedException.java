package com.github.karixdev.youtubethumbnailranking.game.exception;

public class GameHasEndedException extends RuntimeException {
    public GameHasEndedException() {
        super("Game has ended");
    }
}
