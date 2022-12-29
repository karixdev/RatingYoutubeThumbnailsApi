package com.github.karixdev.ratingyoutubethumbnails.thumbnail;

import com.github.karixdev.ratingyoutubethumbnails.ContainersEnvironment;
import com.github.karixdev.ratingyoutubethumbnails.jwt.JwtService;
import com.github.karixdev.ratingyoutubethumbnails.security.UserPrincipal;
import com.github.karixdev.ratingyoutubethumbnails.user.UserRepository;
import com.github.karixdev.ratingyoutubethumbnails.user.UserRole;
import com.github.karixdev.ratingyoutubethumbnails.user.UserService;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import javax.transaction.Transactional;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@WireMockTest(httpPort = 8888)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ThumbnailControllerIT  extends ContainersEnvironment {
    @Autowired
    WebTestClient webClient;

    @Autowired
    JwtService jwtService;

    @Autowired
    ThumbnailRepository thumbnailRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @DynamicPropertySource
    static void overrideYoutubeApiBaseUrl(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("youtube-api.base-url", () -> "http://localhost:8888");
    }

    @AfterEach
    void tearDown() {
        thumbnailRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldNotAddThumbnailForNotAuthorizedUser() {
        String payload = """
                {
                    "youtube_video_id": "youtube-id"
                }
                """;

        webClient.post().uri("/api/v1/thumbnail")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldNotAddAlreadyAddedThumbnail() {
        UserPrincipal userPrincipal = new UserPrincipal(
                userService.createUser(
                        "email@email.pl",
                        "username",
                        "password",
                        UserRole.ROLE_USER,
                        Boolean.TRUE
                ));

        String token = jwtService.createToken(userPrincipal);

        thumbnailRepository.save(Thumbnail.builder()
                .url("thumbnail-url")
                .youtubeVideoId("youtube-id")
                .addedBy(userPrincipal.getUser())
                .build());

        String payload = """
                {
                    "youtube_video_id": "youtube-id"
                }
                """;

        webClient.post().uri("/api/v1/thumbnail")
                .header("Authorization", String.format("Bearer %s", token))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);

        assertThat(thumbnailRepository.findAll()).hasSize(1);
    }

    @Test
    void shouldNotAddNotExistingVideoThumbnail() {
        UserPrincipal userPrincipal = new UserPrincipal(
                userService.createUser(
                        "email@email.pl",
                        "username",
                        "password",
                        UserRole.ROLE_USER,
                        Boolean.TRUE
                ));

        String token = jwtService.createToken(userPrincipal);

        String payload = """
                {
                    "youtube_video_id": "i-do-not-exist"
                }
                """;

        stubFor(get("/videos?id=i-do-not-exist&part=snippet&maxResults=1&key=api-key")
                .willReturn(ok()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                     "kind": "youtube#videoListResponse",
                                     "etag": "RlRThpMOpIat-aidraFmI9pyRgo",
                                     "items": [],
                                     "pageInfo": {
                                         "totalResults": 0,
                                         "resultsPerPage": 0
                                     }
                                 }
                                """)
                )
        );

        webClient.post().uri("/api/v1/thumbnail")
                .header("Authorization", String.format("Bearer %s", token))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isNotFound();

        assertThat(thumbnailRepository.findAll()).isEmpty();
    }

    @Test
    void shouldAddThumbnail() {
        UserPrincipal userPrincipal = new UserPrincipal(
                userService.createUser(
                        "email@email.pl",
                        "username",
                        "password",
                        UserRole.ROLE_USER,
                        Boolean.TRUE
                ));

        String token = jwtService.createToken(userPrincipal);

        String payload = """
                {
                    "youtube_video_id": "dQw4w9WgXcQ"
                }
                """;

        stubFor(get("/videos?id=dQw4w9WgXcQ&part=snippet&maxResults=1&key=api-key")
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

        webClient.post().uri("/api/v1/thumbnail")
                .header("Authorization", String.format("Bearer %s", token))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.youtube_video_id").isEqualTo("dQw4w9WgXcQ")
                .jsonPath("$.url").isEqualTo("https://i.ytimg.com/vi/dQw4w9WgXcQ/maxresdefault.jpg")
                .jsonPath("$.added_by.username").isEqualTo("username");

        Optional<Thumbnail> optionalThumbnail =
                thumbnailRepository.findByYoutubeVideoId("dQw4w9WgXcQ");

        assertThat(optionalThumbnail).isNotEmpty();

        Thumbnail thumbnail = optionalThumbnail.get();

        assertThat(thumbnail.getUrl()).isEqualTo("https://i.ytimg.com/vi/dQw4w9WgXcQ/maxresdefault.jpg");
        assertThat(thumbnail.getYoutubeVideoId()).isEqualTo("dQw4w9WgXcQ");
        assertThat(thumbnail.getAddedBy()).isEqualTo(userPrincipal.getUser());
    }

    @Test
    void shouldNotDeleteNotExistingThumbnail() {
        UserPrincipal userPrincipal = new UserPrincipal(
                userService.createUser(
                        "email@email.pl",
                        "username",
                        "password",
                        UserRole.ROLE_USER,
                        Boolean.TRUE
                ));

        String token = jwtService.createToken(userPrincipal);

        webClient.delete().uri("/api/v1/thumbnail/1")
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldNotDeleteForUserWhoIsNotAnAdminAndNotAnAuthorOfThumbnail() {
        Thumbnail thumbnail = thumbnailRepository.save(Thumbnail.builder()
                .url("thumbnail-url")
                .youtubeVideoId("youtube-id")
                .addedBy(userService.createUser(
                        "email@email.pl",
                        "username",
                        "password",
                        UserRole.ROLE_USER,
                        Boolean.TRUE
                ))
                .build());

        UserPrincipal userPrincipal = new UserPrincipal(
                userService.createUser(
                        "email-2@email.pl",
                        "username-2",
                        "password-2",
                        UserRole.ROLE_USER,
                        Boolean.TRUE
                ));

        String token = jwtService.createToken(userPrincipal);

        webClient.delete().uri("/api/v1/thumbnail/" + thumbnail.getId())
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void shouldDeleteForUserWhoIsAnAdminAndNotAnAuthorOfThumbnail() {
        Thumbnail thumbnail = thumbnailRepository.save(Thumbnail.builder()
                .url("thumbnail-url")
                .youtubeVideoId("youtube-id")
                .addedBy(userService.createUser(
                        "email@email.pl",
                        "username",
                        "password",
                        UserRole.ROLE_USER,
                        Boolean.TRUE
                ))
                .build());

        UserPrincipal userPrincipal = new UserPrincipal(
                userService.createUser(
                        "email-2@email.pl",
                        "username-2",
                        "password-2",
                        UserRole.ROLE_ADMIN,
                        Boolean.TRUE
                ));

        String token = jwtService.createToken(userPrincipal);

        webClient.delete().uri("/api/v1/thumbnail/" + thumbnail.getId())
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("success");
    }

    @Test
    void shouldDeleteForAnAuthorOfThumbnail() {
        UserPrincipal userPrincipal = new UserPrincipal(
                userService.createUser(
                        "email@email.pl",
                        "username",
                        "password",
                        UserRole.ROLE_USER,
                        Boolean.TRUE
                ));

        Thumbnail thumbnail = thumbnailRepository.save(Thumbnail.builder()
                .url("thumbnail-url")
                .youtubeVideoId("youtube-id")
                .addedBy(userPrincipal.getUser())
                .build());

        String token = jwtService.createToken(userPrincipal);

        webClient.delete().uri("/api/v1/thumbnail/" + thumbnail.getId())
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("success");
    }
}
