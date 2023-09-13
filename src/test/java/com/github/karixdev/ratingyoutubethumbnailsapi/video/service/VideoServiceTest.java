package com.github.karixdev.ratingyoutubethumbnailsapi.video.service;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.UserDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video.WriteVideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.youtube.YoutubeVideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.utils.TestUtils;
import com.github.karixdev.ratingyoutubethumbnailsapi.video.entity.Video;
import com.github.karixdev.ratingyoutubethumbnailsapi.video.exception.NotExistingYoutubeVideoException;
import com.github.karixdev.ratingyoutubethumbnailsapi.video.exception.UnavailableYoutubeIdException;
import com.github.karixdev.ratingyoutubethumbnailsapi.video.mapper.VideoMapper;
import com.github.karixdev.ratingyoutubethumbnailsapi.video.repository.VideoRepository;
import com.github.karixdev.ratingyoutubethumbnailsapi.youtube.YoutubeServiceApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VideoServiceTest {

    @InjectMocks
    VideoService underTest;

    @Mock
    VideoRepository repository;

    @Mock
    YoutubeServiceApi youtubeService;

    @Mock
    VideoMapper mapper;

    @Mock
    Clock clock;

    private static final ZonedDateTime NOW = ZonedDateTime.of(
            LocalDate.of(2023, 1, 1),
            LocalTime.of(1, 0),
            ZoneId.of("UTC+0")
    );

    @Test
    void GivenWriteVideoDTOWithYtVideoIdThatIsAlreadySaved_WhenCreate_ThenThrowsUnavailableYoutubeIdException() {
        // Given
        WriteVideoDTO dto = new WriteVideoDTO(null, "youtube-id");

        when(repository.findByYoutubeIdAndNotRemovedState(eq(dto.youtubeId())))
                .thenReturn(Optional.of(TestUtils.createVideo()));

        // When & Then
        assertThatThrownBy(() -> underTest.create(dto))
                .isInstanceOf(UnavailableYoutubeIdException.class);
    }

    @Test
    void GivenWriteVideoDTOWithNotExistingYtVideoId_WhenCreate_ThenThrowsNotExistingYoutubeVideoException() {
        // Given
        WriteVideoDTO dto = new WriteVideoDTO(null, "youtube-id");

        when(repository.findByYoutubeIdAndNotRemovedState(eq(dto.youtubeId())))
                .thenReturn(Optional.empty());

        when(youtubeService.findYoutubeMovieById(eq("youtube-id")))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> underTest.create(dto))
                .isInstanceOf(NotExistingYoutubeVideoException.class);
    }

    @Test
    void GivenWriteVideoDTO_WhenCreate_ThenSavedVideoAndMapsIntoDTO() {
        // Given
        UserDTO userDTO = TestUtils.createUserDTO();
        String youtubeId = "youtube-id";
        WriteVideoDTO dto = new WriteVideoDTO(userDTO, youtubeId);

        when(repository.findByYoutubeIdAndNotRemovedState(eq(dto.youtubeId())))
                .thenReturn(Optional.empty());

        YoutubeVideoDTO youtubeVideoDTO = TestUtils.createYoutubeVideoDTO(youtubeId);
        when(youtubeService.findYoutubeMovieById(eq(youtubeId)))
                .thenReturn(Optional.of(youtubeVideoDTO));

        LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 1, 0);
        Video video = TestUtils.createVideo(null, youtubeId, userDTO.id(), createdAt);

        when(mapper.writeDtoAndYoutubeVideoToEntity(eq(dto), eq(youtubeVideoDTO)))
                .thenReturn(video);

        when(clock.instant())
                .thenReturn(NOW.toInstant());

        when(clock.getZone())
                .thenReturn(NOW.getZone());

        // When
        underTest.create(dto);

        // Then
        verify(repository).save(eq(video));
        verify(mapper).entityToDto(eq(video));
    }
}