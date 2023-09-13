package com.github.karixdev.ratingyoutubethumbnailsapi.video;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.UserDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video.WriteVideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video.VideoDTO;

import java.util.UUID;

public interface VideoServiceApi {
    VideoDTO create(WriteVideoDTO dto);
    VideoDTO delete(UUID id, UserDTO user);
}
