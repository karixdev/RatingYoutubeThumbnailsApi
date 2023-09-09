package com.github.karixdev.ratingyoutubethumbnailsapi.youtube.mapper;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.youtube.YoutubeVideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.youtube.payload.ItemResponse;
import com.github.karixdev.ratingyoutubethumbnailsapi.youtube.payload.SnippetResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.github.karixdev.ratingyoutubethumbnailsapi.youtube.YoutubeTestUtil.createYtApiResponse;
import static org.assertj.core.api.Assertions.assertThat;

class YoutubeVideoMapperTest {
    YoutubeVideoMapper underTest = Mappers.getMapper(YoutubeVideoMapper.class);

    @Test
    void GivenItemResponse_WhenItemToDTO_ThenReturnsCorrectDTO() {
        // Given
        ItemResponse item = createYtApiResponse().items().get(0);
        SnippetResponse snippet = item.snippet();

        // When
        YoutubeVideoDTO result = underTest.itemToDTO(item);

        // Then
        assertThat(result.id()).isEqualTo(item.id());
        assertThat(result.defaultResUrl()).isEqualTo(snippet.thumbnails().defaultRes().url());
        assertThat(result.mediumResUrl()).isEqualTo(snippet.thumbnails().mediumRes().url());
        assertThat(result.highResUrl()).isEqualTo(snippet.thumbnails().highRes().url());
        assertThat(result.standardResUrl()).isEqualTo(snippet.thumbnails().standardRes().url());
        assertThat(result.maxResUrl()).isEqualTo(snippet.thumbnails().maxRes().url());
    }
}