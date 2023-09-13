package com.github.karixdev.ratingyoutubethumbnailsapi.video.mapper;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video.ThumbnailsDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video.VideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video.WriteVideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.youtube.YoutubeVideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.entity.EntityState;
import com.github.karixdev.ratingyoutubethumbnailsapi.utils.TestUtils;
import com.github.karixdev.ratingyoutubethumbnailsapi.video.entity.Video;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class VideoMapperTest {

    VideoMapper underTest = Mappers.getMapper(VideoMapper.class);

    @Test
    void GivenWriteVideoDTOAndYoutubeVideoDTO_WhenWriteDtoAndYoutubeVideoToEntity_ThenReturnsCorrectVideo() {
        // Given
        String youtubeId = "youtube-id";
        WriteVideoDTO writeVideoDTO = new WriteVideoDTO(TestUtils.createUserDTO(), youtubeId);
        YoutubeVideoDTO youtubeVideoDTO = TestUtils.createYoutubeVideoDTO(youtubeId);

        // When
        Video result = underTest.writeDtoAndYoutubeVideoToEntity(writeVideoDTO, youtubeVideoDTO);

        // Then
        assertThat(result.getId()).isNull();
        assertThat(result.getState()).isEqualTo(EntityState.PERSISTED);
        assertThat(result.getUserId()).isEqualTo(writeVideoDTO.user().id());
        assertThat(result.getYoutubeId()).isEqualTo(youtubeId);
        assertThat(result.getDefaultResThumbnail()).isEqualTo(youtubeVideoDTO.defaultResUrl());
        assertThat(result.getMediumResThumbnail()).isEqualTo(youtubeVideoDTO.mediumResUrl());
        assertThat(result.getHighResThumbnail()).isEqualTo(youtubeVideoDTO.highResUrl());
        assertThat(result.getStandardResThumbnail()).isEqualTo(youtubeVideoDTO.standardResUrl());
        assertThat(result.getMaxResThumbnail()).isEqualTo(youtubeVideoDTO.maxResUrl());
    }

    @Test
    void GivenVideo_WhenEntityToDTO_ThenReturnsCorrectDTO() {
        // Given
        Video video = TestUtils.createVideo();

        // When
        VideoDTO result = underTest.entityToDto(video);

        // Then
        assertThat(result.id()).isEqualTo(video.getId());
        assertThat(result.youtubeId()).isEqualTo(video.getYoutubeId());
        assertThat(result.userId()).isEqualTo(video.getUserId());

        ThumbnailsDTO thumbnails = result.thumbnails();

        assertThat(thumbnails.defaultRes()).isEqualTo(video.getDefaultResThumbnail());
        assertThat(thumbnails.mediumRes()).isEqualTo(video.getMediumResThumbnail());
        assertThat(thumbnails.highRes()).isEqualTo(video.getHighResThumbnail());
        assertThat(thumbnails.standardRes()).isEqualTo(video.getStandardResThumbnail());
        assertThat(thumbnails.maxRes()).isEqualTo(video.getMaxResThumbnail());
    }

}