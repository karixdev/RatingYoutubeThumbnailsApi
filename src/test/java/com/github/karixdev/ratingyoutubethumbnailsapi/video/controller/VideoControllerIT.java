package com.github.karixdev.ratingyoutubethumbnailsapi.video.controller;

import com.github.karixdev.ratingyoutubethumbnailsapi.ContainersEnvironment;
import com.github.karixdev.ratingyoutubethumbnailsapi.infrastucture.security.jwt.JwtService;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.UserDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.entity.EntityState;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.entity.User;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.mapper.UserMapper;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.repository.UserRepository;
import com.github.karixdev.ratingyoutubethumbnailsapi.utils.TestUtils;
import com.github.karixdev.ratingyoutubethumbnailsapi.video.entity.Video;
import com.github.karixdev.ratingyoutubethumbnailsapi.video.repository.VideoRepository;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@WireMockTest(httpPort = 9999)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class VideoControllerIT extends ContainersEnvironment {

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtService jwtService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    WebTestClient webClient;

    @Autowired
    EntityManager em;

    static String key = "123";

    @DynamicPropertySource
    static void overrideYtApiProperties(DynamicPropertyRegistry registry) {
        registry.add("youtube-api.base-url", () -> "http://localhost:9999");
        registry.add("youtube-api.key", () -> key);
    }

    @AfterEach
    void tearDown() {
        videoRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldNotAddVideoIfThereIsAlreadyOneWithProvidedYoutubeId() {
        User user = userRepository.save(TestUtils.createUser());
        UserDTO userDTO = userMapper.userToDTO(user);

        String jwt = jwtService.create(userDTO);

        Video video = TestUtils.createVideo();
        videoRepository.save(video);

        String body = """
                {
                    "youtubeId": "%s"
                }
                """.formatted(video.getYoutubeId());

        webClient.post().uri("/api/videos")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest();

        assertThat(videoRepository.count()).isEqualTo(1);
    }

    @Test
    void shouldNotAddVideoThatYoutubeApiReturnsEmptyItemsList() {
        User user = userRepository.save(TestUtils.createUser());
        UserDTO userDTO = userMapper.userToDTO(user);

        String jwt = jwtService.create(userDTO);

        String youtubeId = "youtube-id";

        Video removedVide = TestUtils.createVideo(null, youtubeId, userDTO.id(), LocalDateTime.now(), EntityState.REMOVED);

        videoRepository.save(removedVide);
        videoRepository.save(TestUtils.createVideo(null, "youtube-id-2", userDTO.id(), LocalDateTime.now(), EntityState.PERSISTED));

        stubFor(get(urlPathEqualTo("/videos"))
                .withQueryParams(Map.of(
                        "id", equalTo(youtubeId),
                        "key", equalTo(key)
                ))
                .willReturn(ok().withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE).withBody("""
                        {
                            "items": []
                        }
                        """))
        );

        String body = """
                {
                    "youtubeId": "%s"
                }
                """.formatted(youtubeId);

        webClient.post().uri("/api/videos")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest();

        assertThat(videoRepository.count()).isEqualTo(2);
    }

    @Test
    void shouldAddVideo() {
        User user = userRepository.save(TestUtils.createUser());
        UserDTO userDTO = userMapper.userToDTO(user);

        String jwt = jwtService.create(userDTO);

        String youtubeId = "youtube-id";
        videoRepository.save(TestUtils.createVideo(null, youtubeId, userDTO.id(), LocalDateTime.now(), EntityState.REMOVED));
        videoRepository.save(TestUtils.createVideo(null, "youtube-id-2", userDTO.id(), LocalDateTime.now(), EntityState.PERSISTED));

        stubFor(get(urlPathEqualTo("/videos"))
                .withQueryParams(Map.of(
                        "id", equalTo(youtubeId),
                        "key", equalTo(key)
                ))
                .willReturn(ok()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(TestUtils.ytApiSuccessResponse()))
        );

        String body = """
                {
                    "youtubeId": "%s"
                }
                """.formatted(youtubeId);

        var responseBody = webClient.post().uri("/api/videos")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt)
                .bodyValue(body)
                .exchange()
                .expectStatus().isCreated()
                .expectBody();

        List<Video> allMovies = videoRepository.findAll();
        assertThat(allMovies).hasSize(3);

        Optional<Video> optionalVideo = allMovies.stream()
                .filter(video -> video.getYoutubeId().equals(youtubeId) && video.getState().equals(EntityState.PERSISTED))
                .findFirst();
        assertThat(optionalVideo).isPresent();

        Video resultVideo = optionalVideo.get();

        responseBody.jsonPath("$.id")
                .isEqualTo(resultVideo.getId().toString());

        assertThat(resultVideo.getYoutubeId()).isEqualTo(youtubeId);
        responseBody.jsonPath("$.youtubeId").isEqualTo(youtubeId);

        assertThat(resultVideo.getDefaultResThumbnail()).isEqualTo("default");
        responseBody.jsonPath("$.thumbnails.defaultRes").isEqualTo("default");

        assertThat(resultVideo.getMediumResThumbnail()).isEqualTo("medium");
        responseBody.jsonPath("$.thumbnails.mediumRes").isEqualTo("medium");

        assertThat(resultVideo.getHighResThumbnail()).isEqualTo("high");
        responseBody.jsonPath("$.thumbnails.highRes").isEqualTo("high");

        assertThat(resultVideo.getStandardResThumbnail()).isEqualTo("standard");
        responseBody.jsonPath("$.thumbnails.standardRes").isEqualTo("standard");

        assertThat(resultVideo.getMaxResThumbnail()).isEqualTo("max");
        responseBody.jsonPath("$.thumbnails.maxRes").isEqualTo("max");
    }

    @Test
    void shouldNotDeleteNotExistingVideo() {
        User user = userRepository.save(TestUtils.createUser());
        UserDTO userDTO = userMapper.userToDTO(user);

        String jwt = jwtService.create(userDTO);

        webClient.delete().uri("/api/videos/" + UUID.randomUUID())
                .header("Authorization", "Bearer " + jwt)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldNotDeleteVideoUploadedByOtherUser() {
        User user = userRepository.save(TestUtils.createUser());
        UserDTO userDTO = userMapper.userToDTO(user);

        Video video = videoRepository.save(TestUtils.createVideo("ytId", UUID.randomUUID()));

        String jwt = jwtService.create(userDTO);

        webClient.delete().uri("/api/videos/" + video.getId())
                .header("Authorization", "Bearer " + jwt)
                .exchange()
                .expectStatus().isForbidden();

        Optional<Video> optionalVideo = videoRepository.findById(video.getId());
        assertThat(optionalVideo).isPresent();

        assertThat(optionalVideo.get().getState()).isEqualTo(EntityState.PERSISTED);
    }

    @Test
    void shouldDeleteVideoUploadedByOtherUserForAdmin() {
        User user = userRepository.save(TestUtils.createAdmin());
        UserDTO userDTO = userMapper.userToDTO(user);

        Video video = videoRepository.save(TestUtils.createVideo("ytId", userDTO.id()));

        String jwt = jwtService.create(userDTO);

        webClient.delete().uri("/api/videos/" + video.getId())
                .header("Authorization", "Bearer " + jwt)
                .exchange()
                .expectStatus().isNoContent();

        Optional<Video> optionalVideo = videoRepository.findById(video.getId());
        assertThat(optionalVideo).isPresent();

        assertThat(optionalVideo.get().getState()).isEqualTo(EntityState.REMOVED);
    }

    @Test
    void shouldDeleteVideo() {
        User user = userRepository.save(TestUtils.createUser());
        UserDTO userDTO = userMapper.userToDTO(user);

        Video video = videoRepository.save(TestUtils.createVideo("ytId", userDTO.id()));

        String jwt = jwtService.create(userDTO);

        webClient.delete().uri("/api/videos/" + video.getId())
                .header("Authorization", "Bearer " + jwt)
                .exchange()
                .expectStatus().isNoContent();

        Optional<Video> optionalVideo = videoRepository.findById(video.getId());
        assertThat(optionalVideo).isPresent();

        assertThat(optionalVideo.get().getState()).isEqualTo(EntityState.REMOVED);
    }

}