package com.github.karixdev.ratingyoutubethumbnailsapi.youtube.service;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.youtube.YoutubeVideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.youtube.client.YoutubeApiClient;
import com.github.karixdev.ratingyoutubethumbnailsapi.youtube.mapper.YoutubeVideoMapper;
import com.github.karixdev.ratingyoutubethumbnailsapi.youtube.payload.ItemResponse;
import com.github.karixdev.ratingyoutubethumbnailsapi.youtube.payload.YoutubeApiVideosResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.github.karixdev.ratingyoutubethumbnailsapi.youtube.YoutubeTestUtil.createYoutubeVideDTO;
import static com.github.karixdev.ratingyoutubethumbnailsapi.youtube.YoutubeTestUtil.createYtApiResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class YoutubeServiceTest {

    YoutubeService underTest;

    YoutubeApiClient apiClient = mock(YoutubeApiClient.class);
    YoutubeVideoMapper mapper = mock(YoutubeVideoMapper.class);
    String key;

    @BeforeEach
    void setUp() {
        apiClient = mock(YoutubeApiClient.class);
        mapper = mock(YoutubeVideoMapper.class);
        key = "123";

        underTest = new YoutubeService(apiClient, mapper, key);
    }

    @Test
    void GivenIdThatYoutubeApiReturnsNoResults_WhenFindYoutubeMovieById_ThenReturnsEmptyOptional() {
        // Given
        String id = "id";

        when(apiClient.findMovies(eq(id), eq(key)))
                .thenReturn(createYtApiResponse());

        // When
        Optional<YoutubeVideoDTO> result = underTest.findYoutubeMovieById(id);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void GivenIdThatYoutubeApiReturnsMoreThanOneResult_WhenFindYoutubeMovieById_ThenReturnsEmptyOptional() {
        // Given
        String id = "id";

        when(apiClient.findMovies(eq(id), eq(key)))
                .thenReturn(createYtApiResponse(null, null));

        // When
        Optional<YoutubeVideoDTO> result = underTest.findYoutubeMovieById(id);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void GivenIdThatYoutubeApiReturnsExactlyOneResult_WhenFindYoutubeMovieById_ThenReturnsMapsToDTO() {
        // Given
        String id = "id";

        YoutubeApiVideosResponse apiVideosResponse = createYtApiResponse();
        ItemResponse item = apiVideosResponse.items().get(0);

        when(apiClient.findMovies(eq(id), eq(key)))
                .thenReturn(createYtApiResponse());

        YoutubeVideoDTO dto = createYoutubeVideDTO();
        when(mapper.itemToDTO(eq(item)))
                .thenReturn(dto);

        // When
        Optional<YoutubeVideoDTO> result = underTest.findYoutubeMovieById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(dto);
    }
}