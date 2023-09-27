package com.github.karixdev.ratingyoutubethumbnailsapi.game.exception;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.AppException;
import org.springframework.http.HttpStatus;

public class InsufficientNumberOfAvailableVideos extends AppException {
    public InsufficientNumberOfAvailableVideos() {
        super("Insufficient number of available videos", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
