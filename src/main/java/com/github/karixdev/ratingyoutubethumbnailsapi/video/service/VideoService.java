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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class VideoService implements VideoServiceApi {
    private final VideoRepository repository;
    private final YoutubeServiceApi youtubeService;
    private final VideoMapper mapper;
    private final Clock clock;

    private static final Random random = new Random();

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

    @Override
    public Optional<VideoDTO> findById(UUID id) {
        return repository.findById(id).map(mapper::entityToDto);
    }

    @Override
    public Optional<VideoDTO> findRandom(List<UUID> ids, boolean excludeOrNarrow) {
        Function<List<UUID>, Integer> countFunc = repository::countByIdNotIn;
        BiFunction<List<UUID>, PageRequest, Page<Video>> paginationFunc = repository::findByIdNotIn;

        if (!excludeOrNarrow) {
            countFunc = repository::countByIdIn;
            paginationFunc = repository::findByIdIn;
        }

        int count = countFunc.apply(ids);
        if (count <= 0) {
            return Optional.empty();
        }

        PageRequest pageRequest = PageRequest.of(random.nextInt(count), 1);
        Page<Video> rndVideoPage = paginationFunc.apply(ids, pageRequest);

        return Optional.of(rndVideoPage.getContent().get(0)).map(mapper::entityToDto);
    }

}
