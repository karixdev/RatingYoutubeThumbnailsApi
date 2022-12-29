package com.github.karixdev.ratingyoutubethumbnails.youtube.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class YoutubeApiEmptyResponseException extends RuntimeException {
    public YoutubeApiEmptyResponseException() {
        super("Youtube API responded with empty body");
    }
}
