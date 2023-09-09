package com.github.karixdev.ratingyoutubethumbnailsapi.youtube.client;

import com.github.karixdev.ratingyoutubethumbnailsapi.ContainersEnvironment;
import com.github.karixdev.ratingyoutubethumbnailsapi.youtube.exception.YoutubeApiException;
import com.github.karixdev.ratingyoutubethumbnailsapi.youtube.payload.YoutubeApiVideosResponse;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Map;

import static com.github.karixdev.ratingyoutubethumbnailsapi.youtube.YoutubeTestUtil.createYtApiResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@WireMockTest(httpPort = 9999)
class YoutubeApiClientIT extends ContainersEnvironment {
    @Autowired
    YoutubeApiClient underTest;

    static String key = "123";

    @DynamicPropertySource
    static void overrideYtApiProperties(DynamicPropertyRegistry registry) {
        registry.add("youtube-api.base-url", () -> "http://localhost:9999");
        registry.add("youtube-api.key", () -> key);
    }

    @Test
    void GivenIdThatYoutubeAPIReturnsError_WhenFindMovies_ThenThrowsYoutubeApiException() {
        // Given
        String id = "id";

        stubFor(get(urlPathEqualTo("/videos"))
                .withQueryParams(Map.of(
                        "id", equalTo(id),
                        "key", equalTo(key)
                ))
                .willReturn(badRequest().withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE).withBody("""
                        {
                            "message": "error message"
                        }
                        """))
        );

        // When & Then
        assertThatThrownBy(() -> underTest.findMovies(id, key))
                .isInstanceOf(YoutubeApiException.class);
    }

    @Test
    void GivenIdAndKey_WhenFindMovies_ThenReturnsCorrectDTO() {
        // Given
        String id = "id";

        stubFor(get(urlPathEqualTo("/videos"))
                .withQueryParams(Map.of(
                        "id", equalTo(id),
                        "key", equalTo(key)
                ))
                .willReturn(ok()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                        {
                            "items": [
                                {
                                    "kind": "kind",
                                    "id": "id",
                                    "snippet": {
                                        "title": "title",
                                        "description": "description",
                                        "thumbnails": {
                                            "default": {
                                                     "url": "default",
                                                     "width": 100,
                                                     "height": 100
                                            },
                                            "medium": {
                                                     "url": "medium",
                                                     "width": 100,
                                                     "height": 100
                                            },
                                            "high": {
                                                     "url": "high",
                                                     "width": 100,
                                                     "height": 100
                                            },
                                            "standard": {
                                                     "url": "standard",
                                                     "width": 100,
                                                     "height": 100
                                            },
                                            "maxres": {
                                                     "url": "max",
                                                     "width": 100,
                                                     "height": 100
                                            }
                                        }
                                    }
                                }
                            ]
                        }
                        """))
        );

        // When
        YoutubeApiVideosResponse result = underTest.findMovies(id, key);

        // Then
        YoutubeApiVideosResponse expected = createYtApiResponse();
        assertThat(result).isEqualTo(expected);
    }
}