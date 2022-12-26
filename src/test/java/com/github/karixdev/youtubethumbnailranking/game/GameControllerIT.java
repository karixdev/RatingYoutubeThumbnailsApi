package com.github.karixdev.youtubethumbnailranking.game;

import com.github.karixdev.youtubethumbnailranking.game.payload.response.GameResponse;
import com.github.karixdev.youtubethumbnailranking.jwt.JwtService;
import com.github.karixdev.youtubethumbnailranking.rating.Rating;
import com.github.karixdev.youtubethumbnailranking.rating.RatingProperties;
import com.github.karixdev.youtubethumbnailranking.rating.RatingRepository;
import com.github.karixdev.youtubethumbnailranking.security.UserPrincipal;
import com.github.karixdev.youtubethumbnailranking.thumnail.Thumbnail;
import com.github.karixdev.youtubethumbnailranking.thumnail.ThumbnailRepository;
import com.github.karixdev.youtubethumbnailranking.user.UserRepository;
import com.github.karixdev.youtubethumbnailranking.user.UserRole;
import com.github.karixdev.youtubethumbnailranking.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Clock;
import java.time.LocalDateTime;

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
    GameService gameService;

    @Autowired
    GameRepository gameRepository;

    @Autowired
    ThumbnailRepository thumbnailRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RatingRepository ratingRepository;

    @Autowired
    RatingProperties ratingProperties;

    @Autowired
    Clock clock;

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

    @Test
    void shouldRespondWith404GivenNotExistingGameId() {
        UserPrincipal userPrincipal = new UserPrincipal(
                userService.createUser(
                        "email@email.pl",
                        "username",
                        "password",
                        UserRole.ROLE_USER,
                        Boolean.TRUE
                ));

        String payload = """
                {
                    "winner_id": 1
                }
                """;

        String token = jwtService.createToken(userPrincipal);

        webClient.post().uri("/api/v1/game/result/121")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldRespondWith403WhenUserWhoIsNotOwnerOfGameTriesToResult() {
        UserPrincipal userPrincipal = new UserPrincipal(
                userService.createUser(
                        "email@email.pl",
                        "username",
                        "password",
                        UserRole.ROLE_USER,
                        Boolean.TRUE
                ));

        UserPrincipal otherPrincipal = new UserPrincipal(
                userService.createUser(
                        "email-2@email.pl",
                        "username-2",
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

        GameResponse gameResponse = gameService.start(userPrincipal);

        String token = jwtService.createToken(otherPrincipal);

        String payload = """
                {
                    "winner_id": 1
                }
                """;

        webClient.post().uri("/api/v1/game/result/" + gameResponse.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void shouldRespondWith409WhenGameHasEnded() {
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

        GameResponse gameResponse = gameService.start(userPrincipal);

        Game game = gameRepository.findById(gameResponse.getId()).orElseThrow();
        game.setLastActivity(LocalDateTime.now(clock).minusDays(1));

        gameRepository.save(game);

        String token = jwtService.createToken(userPrincipal);

        String payload = """
                {
                    "winner_id": 1
                }
                """;

        webClient.post().uri("/api/v1/game/result/" + gameResponse.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void shouldRespondWith400GivenInvalidWinnerId() {
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

        GameResponse gameResponse = gameService.start(userPrincipal);

        String token = jwtService.createToken(userPrincipal);

        String payload = """
                {
                    "winner_id": %d
                }
                """;
        payload = String.format(payload, gameResponse.getThumbnail1().getId().intValue() + gameResponse.getThumbnail2().getId().intValue());

        webClient.post().uri("/api/v1/game/result/" + gameResponse.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldResult() {
        UserPrincipal userPrincipal = new UserPrincipal(
                userService.createUser(
                        "email@email.pl",
                        "username",
                        "password",
                        UserRole.ROLE_USER,
                        Boolean.TRUE
                ));

        for (int i = 0; i < 3; i++) {
            thumbnailRepository.save(Thumbnail.builder()
                    .addedBy(userPrincipal.getUser())
                    .url("thumbnail-url-" + i)
                    .youtubeVideoId("youtube-id-" + i)
                    .build());
        }

        GameResponse gameResponse = gameService.start(userPrincipal);
        Game game = gameRepository.findById(gameResponse.getId()).orElseThrow();

        String token = jwtService.createToken(userPrincipal);

        String payload = """
                {
                    "winner_id": %d
                }
                """;
        payload = String.format(payload, gameResponse.getThumbnail1().getId());

        webClient.post().uri("/api/v1/game/result/" + gameResponse.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isOk();

        Game updatedGame = gameRepository.findById(gameResponse.getId()).orElseThrow();

        assertThat(updatedGame.getThumbnail1().getId())
                .isEqualTo(gameResponse.getThumbnail1().getId());

        assertThat(updatedGame.getThumbnail2().getId())
                .isNotEqualTo(gameResponse.getThumbnail2().getId());

        Rating rating1 = ratingRepository.findByThumbnailAndUser(
                game.getThumbnail1(), userPrincipal.getUser()).orElseThrow();
        Rating rating2 = ratingRepository.findByThumbnailAndUser(
                game.getThumbnail2(), userPrincipal.getUser()).orElseThrow();

        assertThat(rating1.getPoints()).isGreaterThan(ratingProperties.getBasePoints());
        assertThat(rating2.getPoints()).isLessThan(ratingProperties.getBasePoints());
    }
}
