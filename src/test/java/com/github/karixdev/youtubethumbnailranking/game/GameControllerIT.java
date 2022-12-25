package com.github.karixdev.youtubethumbnailranking.game;

import com.github.karixdev.youtubethumbnailranking.jwt.JwtService;
import com.github.karixdev.youtubethumbnailranking.security.UserPrincipal;
import com.github.karixdev.youtubethumbnailranking.thumnail.Thumbnail;
import com.github.karixdev.youtubethumbnailranking.thumnail.ThumbnailRepository;
import com.github.karixdev.youtubethumbnailranking.thumnail.ThumbnailService;
import com.github.karixdev.youtubethumbnailranking.user.UserRepository;
import com.github.karixdev.youtubethumbnailranking.user.UserRole;
import com.github.karixdev.youtubethumbnailranking.user.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
public class GameControllerIT {
    @Autowired
    WebTestClient webClient;

    @Autowired
    UserService userService;

    @Autowired
    JwtService jwtService;

    @Autowired
    ThumbnailService thumbnailService;

    @Autowired
    GameRepository gameRepository;

    @Autowired
    ThumbnailRepository thumbnailRepository;

    @Autowired
    UserRepository userRepository;

    @AfterEach
    void tearDown() {
        gameRepository.deleteAll();
        thumbnailRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldStartGame() {
        UserPrincipal userPrincipal = new UserPrincipal(
                userService.createUser(
                        "email@email.pl",
                        "username",
                        "password",
                        UserRole.ROLE_USER,
                        Boolean.TRUE
                ));

        for (int i = 0; i < 2; i++) {
            thumbnailRepository.save(Thumbnail.builder()
                    .addedBy(userPrincipal.getUser())
                    .url("thumbnail-url-" + i)
                    .youtubeVideoId("youtube-id-" + i)
                    .build());
        }

        String token = jwtService.createToken(userPrincipal);

        webClient.post().uri("/api/v1/game/start")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.thumbnail1.id").isNotEmpty()
                .jsonPath("$.thumbnail1.url").isNotEmpty()
                .jsonPath("$.thumbnail2.id").isNotEmpty()
                .jsonPath("$.thumbnail2.url").isNotEmpty();

        assertThat(gameRepository.findAll()).hasSize(1);

        Game game = gameRepository.findAll().get(0);

        assertThat(game.getUser()).isEqualTo(userPrincipal.getUser());
        assertThat(game.getThumbnail1()).isNotNull();
        assertThat(game.getThumbnail2()).isNotNull();
    }

    @Test
    void shouldNotStartGameBecauseThereIsOne() {
        UserPrincipal userPrincipal = new UserPrincipal(
                userService.createUser(
                        "email@email.pl",
                        "username",
                        "password",
                        UserRole.ROLE_USER,
                        Boolean.TRUE
                ));

        for (int i = 0; i < 2; i++) {
            thumbnailRepository.save(Thumbnail.builder()
                    .addedBy(userPrincipal.getUser())
                    .url("thumbnail-url-" + i)
                    .youtubeVideoId("youtube-id-" + i)
                    .build());
        }

        String token = jwtService.createToken(userPrincipal);

        webClient.post().uri("/api/v1/game/start")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk();

        webClient.post().uri("/api/v1/game/start")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isBadRequest();
    }
}
