package com.github.karixdev.ratingyoutubethumbnailsapi.youtube.exception;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.AppException;
import org.springframework.http.HttpStatus;

public class YoutubeApiException extends AppException {
    public YoutubeApiException() {
        super("Youtube API returned error status", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
