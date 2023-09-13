package com.github.karixdev.ratingyoutubethumbnailsapi.video.exception;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.ValidationException;

public class NotExistingYoutubeVideoException extends ValidationException {
    public NotExistingYoutubeVideoException() {
        super("youtubeId", "Youtube video with provided id does not exist");
    }
}
