package com.github.karixdev.ratingyoutubethumbnailsapi.video.service;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.UserDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video.VideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video.WriteVideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.youtube.YoutubeVideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.ResourceNotFoundException;
import com.github.karixdev.ratingyoutubethumbnailsapi.video.VideoServiceApi;
import com.github.karixdev.ratingyoutubethumbnailsapi.video.entity.Video;
import com.github.karixdev.ratingyoutubethumbnailsapi.video.exception.NotExistingYoutubeVideoException;
import com.github.karixdev.ratingyoutubethumbnailsapi.video.exception.UnavailableYoutubeIdException;
import com.github.karixdev.ratingyoutubethumbnailsapi.video.mapper.VideoMapper;
import com.github.karixdev.ratingyoutubethumbnailsapi.video.repository.VideoRepository;
import com.github.karixdev.ratingyoutubethumbnailsapi.youtube.YoutubeServiceApi;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoService implements VideoServiceApi {
    private final VideoRepository repository;
    private final YoutubeServiceApi youtubeService;
    private final VideoMapper mapper;
    private final Clock clock;

    @Transactional
    @Override
    public VideoDTO create(WriteVideoDTO dto) {
        repository.findByYoutubeIdAndNotRemovedState(dto.youtubeId()).ifPresent(video -> {
            throw new UnavailableYoutubeIdException();
        });

        YoutubeVideoDTO youtubeVideo = youtubeService.findYoutubeMovieById(dto.youtubeId())
                .orElseThrow(NotExistingYoutubeVideoException::new);

        Video video = mapper.writeDtoAndYoutubeVideoToEntity(dto, youtubeVideo);
        video.setCreatedAt(LocalDateTime.now(clock));

        repository.save(video);

        return mapper.entityToDto(video);
    }

    @Transactional
    @Override
    public void delete(UUID id, UserDTO user) {
        Video video = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video with provided id not found"));

        video.moveIntoRemovedState(user);
    }

}
