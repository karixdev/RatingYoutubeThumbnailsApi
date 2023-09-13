package com.github.karixdev.ratingyoutubethumbnailsapi.video;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video.WriteVideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video.VideoDTO;

public interface VideoServiceApi {
    VideoDTO create(WriteVideoDTO dto);
}
