package com.github.karixdev.ratingyoutubethumbnailsapi.video.exception;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.ValidationException;

public class UnavailableYoutubeIdException extends ValidationException {
    public UnavailableYoutubeIdException() {
        super("youtubeId", "There is already video with provided Youtube id");
    }
}
