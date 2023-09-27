package com.github.karixdev.ratingyoutubethumbnailsapi.video;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.UserDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video.WriteVideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video.VideoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VideoServiceApi {
    VideoDTO create(WriteVideoDTO dto);
    void delete(UUID id, UserDTO user);
    Optional<VideoDTO> findById(UUID id);
    Optional<VideoDTO> findRandom(List<UUID> ids, boolean excludeOrNarrow);

}
