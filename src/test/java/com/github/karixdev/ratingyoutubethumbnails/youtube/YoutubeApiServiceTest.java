package com.github.karixdev.ratingyoutubethumbnails.youtube;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class YoutubeApiServiceTest {
    @InjectMocks
    YoutubeApiService underTest;

    @Mock
    YoutubeApiProperties apiProperties;

    @Test
    void GivenEndpointAntQueryParams_WhenCreateUri_ThenReturnsCorrectUri() throws URISyntaxException {
        // Given
        String endpoint = "/videos";
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        queryParams.add("part", "snippet");
        queryParams.add("maxResults", "1");
        queryParams.add("id", "OsKkxVwYMDc");

        when(apiProperties.getApiKey())
                .thenReturn("api-key");

        when(apiProperties.getBaseUrl())
                .thenReturn("https://youtube.googleapis.com/youtube/v3");

        // When
        String result = underTest.createUri(endpoint, queryParams);

        // Then
        URI resultUri = new URI(result);

        assertThat(resultUri).hasParameter("part", "snippet");
        assertThat(resultUri).hasParameter("id", "OsKkxVwYMDc");
        assertThat(resultUri).hasParameter("maxResults", "1");
        assertThat(resultUri).hasParameter("key", "api-key");
        assertThat(resultUri).hasPath("/youtube/v3/videos");
    }
}
