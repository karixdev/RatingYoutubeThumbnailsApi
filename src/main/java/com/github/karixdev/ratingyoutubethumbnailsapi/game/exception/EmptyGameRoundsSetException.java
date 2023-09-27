package com.github.karixdev.ratingyoutubethumbnailsapi.game.exception;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.AppException;
import org.springframework.http.HttpStatus;

public class EmptyGameRoundsSetException extends AppException {
    public EmptyGameRoundsSetException() {
        super("Could not find game rounds", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
