package com.github.karixdev.ratingyoutubethumbnailsapi.video.service;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.UserDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video.VideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video.WriteVideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.youtube.YoutubeVideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.entity.EntityState;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.ResourceNotFoundException;
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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.map;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
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

    @Test
    void GivenIdOfNotExistingVideo_WhenDelete_ThenThrowsResourceNotFoundException() {
        // Given
        UUID id = UUID.randomUUID();

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> underTest.delete(id, null))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Video with provided id not found");
    }

    @Test
    void GivenIdAndUserDTO_WhenDelete_ThenMovesVideoIntoRemovedStatus() {
        // Given
        UserDTO userDTO = TestUtils.createUserDTO();
        Video video = TestUtils.createVideo("ytId", userDTO.id());
        UUID id = video.getId();

        when(repository.findById(id))
                .thenReturn(Optional.of(video));

        // When
        underTest.delete(id, userDTO);

        // Then
        assertThat(video.getState()).isEqualTo(EntityState.REMOVED);
    }

    @Test
    void GivenListOfIdsAndFalse_WhenFindRandom_ThenFindsRandomVideoWhichIdIsInListOfIds() {
        // Given
        List<UUID> ids = List.of(UUID.randomUUID(), UUID.randomUUID());
        when(repository.countByIdIn(ids)).thenReturn(2);

        Video video1 = TestUtils.createVideo(ids.get(0), "ytId1");

        when(repository.findByIdIn(eq(ids), any()))
                .thenReturn(new PageImpl<>(List.of(video1)));

        // When
        underTest.findRandom(ids, false);

        // Then
        verify(mapper).entityToDto(eq(video1));

        verify(repository, never()).countByIdNotIn(any());
        verify(repository, never()).findByIdNotIn(any(), any());
    }

    @Test
    void GivenListOfIdsAndTrue_WhenFindRandom_ThenFindsRandomVideoWhichIdIsNotInListOfIds() {
        // Given
        List<UUID> ids = List.of(UUID.randomUUID(), UUID.randomUUID());
        when(repository.countByIdNotIn(ids)).thenReturn(2);

        Video video1 = TestUtils.createVideo(ids.get(0), "ytId1");

        when(repository.findByIdNotIn(eq(ids), any()))
                .thenReturn(new PageImpl<>(List.of(video1)));

        // When
        underTest.findRandom(ids, true);

        // Then
        verify(mapper).entityToDto(eq(video1));

        verify(repository, never()).countByIdIn(any());
        verify(repository, never()).findByIdIn(any(), any());
    }

    @Test
    void GivenListOfIdsSoThatCountMethodReturnsZero_WhenFindRandom_ThenReturnsEmptyOptional() {
        // Given
        List<UUID> ids = List.of(UUID.randomUUID(), UUID.randomUUID());
        
        when(repository.countByIdNotIn(eq(ids))).thenReturn(0);

        // When
        Optional<VideoDTO> result = underTest.findRandom(ids, true);

        // Then
        assertThat(result).isEmpty();
    }

}