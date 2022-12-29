package com.github.karixdev.ratingyoutubethumbnails.youtube;

import com.github.karixdev.ratingyoutubethumbnails.youtube.exception.YoutubeApiEmptyResponseException;
import com.github.karixdev.ratingyoutubethumbnails.youtube.exception.YoutubeVideoNotFoundException;
import com.github.karixdev.ratingyoutubethumbnails.youtube.payload.request.ItemRequest;
import com.github.karixdev.ratingyoutubethumbnails.youtube.payload.request.SnippetRequest;
import com.github.karixdev.ratingyoutubethumbnails.youtube.payload.request.ThumbnailsRequest;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@WireMockTest(httpPort = 8888)
public class YoutubeVideoServiceTest {
    YoutubeVideoService underTest;

    YoutubeApiService apiService;

    @BeforeEach
    void setUp() {
        apiService = mock(YoutubeApiService.class);
        WebClient webClient = WebClient.builder()
                .build();

        underTest = new YoutubeVideoService(apiService, webClient);
    }

    @Test
    void GivenIdThatYouTubeApiResponsesWithEmptyBodyMovieId_WhenGetVideoDetails_ThenThrowsYoutubeVideoNotFoundException() {
        // Given
        String id = "empty-body";

        when(apiService.createUri(Mockito.any(), Mockito.any()))
                .thenReturn("http://localhost:8888/videos?id=empty-body&part=snippet&maxResults=1&key=test-key");


        stubFor(get("/videos?id=empty-body&part=snippet&maxResults=1&key=test-key")
                .willReturn(ok())
        );

        // When & Then
        assertThatThrownBy(() -> underTest.getVideoDetails(id))
                .isInstanceOf(YoutubeApiEmptyResponseException.class)
                .hasMessage("Youtube API responded with empty body");
    }

    @Test
    void GivenNotExistingYouTubeMovieId_WhenGetVideoDetails_ThenThrowsYoutubeVideoNotFoundException() {
        // Given
        String id = "not-existing";

        when(apiService.createUri(Mockito.any(), Mockito.any()))
                .thenReturn("http://localhost:8888/videos?id=not-existing&part=snippet&maxResults=1&key=test-key");

        stubFor(get("/videos?id=not-existing&part=snippet&maxResults=1&key=test-key")
                .willReturn(ok()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "kind": "youtube#videoListResponse",
                                    "etag": "YIUPVpqNjppyCWOZfL-19bLb7uk",
                                    "items": [],
                                    "pageInfo": {
                                        "totalResults": 0,
                                        "resultsPerPage": 0
                                    }
                                }
                                """)
                )
        );

        // When & Then
        assertThatThrownBy(() -> underTest.getVideoDetails(id))
                .isInstanceOf(YoutubeVideoNotFoundException.class)
                .hasMessage("Youtube video with provided id not found");
    }

    @Test
    void GivenCorrectId_WhenGetVideoDetails_ThenReturnsCorrectItem() {
        // Given
        String id = "dQw4w9WgXcQ";

        when(apiService.createUri(Mockito.any(), Mockito.any()))
                .thenReturn("http://localhost:8888/videos?id=dQw4w9WgXcQ&part=snippet&maxResults=1&key=test-key");

        stubFor(get("/videos?id=dQw4w9WgXcQ&part=snippet&maxResults=1&key=test-key")
                .willReturn(ok()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                     "kind": "youtube#videoListResponse",
                                     "etag": "RlRThpMOpIat-aidraFmI9pyRgo",
                                     "items": [
                                         {
                                             "kind": "youtube#video",
                                             "etag": "al4sIvaQIglR45MbiiREXT6QnyA",
                                             "id": "dQw4w9WgXcQ",
                                             "snippet": {
                                                 "publishedAt": "2009-10-25T06:57:33Z",
                                                 "channelId": "UCuAXFkgsw1L7xaCfnd5JJOw",
                                                 "title": "Music video",
                                                 "description": "Description",
                                                 "thumbnails": {
                                                     "default": {
                                                         "url": "https://i.ytimg.com/vi/dQw4w9WgXcQ/default.jpg",
                                                         "width": 120,
                                                         "height": 90
                                                     },
                                                     "medium": {
                                                         "url": "https://i.ytimg.com/vi/dQw4w9WgXcQ/mqdefault.jpg",
                                                         "width": 320,
                                                         "height": 180
                                                     },
                                                     "high": {
                                                         "url": "https://i.ytimg.com/vi/dQw4w9WgXcQ/hqdefault.jpg",
                                                         "width": 480,
                                                         "height": 360
                                                     },
                                                     "standard": {
                                                         "url": "https://i.ytimg.com/vi/dQw4w9WgXcQ/sddefault.jpg",
                                                         "width": 640,
                                                         "height": 480
                                                     },
                                                     "maxres": {
                                                         "url": "https://i.ytimg.com/vi/dQw4w9WgXcQ/maxresdefault.jpg",
                                                         "width": 1280,
                                                         "height": 720
                                                     }
                                                 },
                                                 "channelTitle": "Channel title",
                                                 "tags": [
                                                     "tag"
                                                 ],
                                                 "categoryId": "10",
                                                 "liveBroadcastContent": "none",
                                                 "defaultLanguage": "en",
                                                 "localized": {
                                                     "title": "Music video",
                                                     "description": "Description"
                                                 },
                                                 "defaultAudioLanguage": "en"
                                             }
                                         }
                                     ],
                                     "pageInfo": {
                                         "totalResults": 1,
                                         "resultsPerPage": 1
                                     }
                                 }
                                """)
                )
        );

        // When
        ItemRequest result = underTest.getVideoDetails(id);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getSnippet()).isNotNull();

        SnippetRequest resultSnippet = result.getSnippet();

        assertThat(resultSnippet.getTitle()).isEqualTo("Music video");
        assertThat(resultSnippet.getDescription()).isEqualTo("Description");

        ThumbnailsRequest resultThumbnails = resultSnippet.getThumbnails();

        assertThat(resultThumbnails.get_default().getUrl()).isEqualTo("https://i.ytimg.com/vi/dQw4w9WgXcQ/default.jpg");
        assertThat(resultThumbnails.getMedium().getUrl()).isEqualTo("https://i.ytimg.com/vi/dQw4w9WgXcQ/mqdefault.jpg");
        assertThat(resultThumbnails.getHigh().getUrl()).isEqualTo("https://i.ytimg.com/vi/dQw4w9WgXcQ/hqdefault.jpg");
        assertThat(resultThumbnails.getStandard().getUrl()).isEqualTo("https://i.ytimg.com/vi/dQw4w9WgXcQ/sddefault.jpg");
        assertThat(resultThumbnails.getMaxres().getUrl()).isEqualTo("https://i.ytimg.com/vi/dQw4w9WgXcQ/maxresdefault.jpg");
    }
}
