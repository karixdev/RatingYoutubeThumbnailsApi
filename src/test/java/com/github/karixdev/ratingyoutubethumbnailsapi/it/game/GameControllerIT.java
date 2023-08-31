package com.github.karixdev.ratingyoutubethumbnailsapi.it.game;

import com.github.karixdev.ratingyoutubethumbnailsapi.game.Game;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.GameRepository;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.GameService;
import com.github.karixdev.ratingyoutubethumbnailsapi.it.ContainersEnvironment;
import com.github.karixdev.ratingyoutubethumbnailsapi.jwt.JwtService;
import com.github.karixdev.ratingyoutubethumbnailsapi.rating.Rating;
import com.github.karixdev.ratingyoutubethumbnailsapi.rating.RatingProperties;
import com.github.karixdev.ratingyoutubethumbnailsapi.rating.RatingRepository;
import com.github.karixdev.ratingyoutubethumbnailsapi.round.RoundRepository;
import com.github.karixdev.ratingyoutubethumbnailsapi.security.UserPrincipal;
import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.Thumbnail;
import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.ThumbnailRepository;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.User;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.UserRepository;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.UserRole;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class GameControllerIT extends ContainersEnvironment {
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
    RoundRepository roundRepository;

    @Autowired
    Clock clock;

    @AfterEach
    void tearDown() {
        roundRepository.deleteAll();
        gameRepository.deleteAll();
        thumbnailRepository.deleteAll();
        userRepository.deleteAll();
    }

//    @Test
//    void shouldStartGame() {
//        UserPrincipal userPrincipal = new UserPrincipal(
//                userService.createUser(
//                        "email@email.pl",
//                        "username",
//                        "password",
//                        UserRole.ROLE_USER,
//                        Boolean.TRUE
//                ));
//
//        for (int i = 0; i < 2; i++) {
//            thumbnailRepository.save(Thumbnail.builder()
//                    .addedBy(userPrincipal.getUser())
//                    .url("thumbnail-url-" + i)
//                    .youtubeVideoId("youtube-id-" + i)
//                    .build());
//        }
//
//        String token = jwtService.createToken(userPrincipal);
//
//        webClient.post().uri("/api/v1/game/start")
//                .header("Authorization", "Bearer " + token)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.id").isNotEmpty()
//                .jsonPath("$.thumbnail1.id").isNotEmpty()
//                .jsonPath("$.thumbnail1.url").isNotEmpty()
//                .jsonPath("$.thumbnail2.id").isNotEmpty()
//                .jsonPath("$.thumbnail2.url").isNotEmpty();
//
//        assertThat(gameRepository.findAll()).hasSize(1);
//
//        Game game = gameRepository.findAll().get(0);
//
//        assertThat(game.getUser()).isEqualTo(userPrincipal.getUser());
//        assertThat(game.getThumbnail1()).isNotNull();
//        assertThat(game.getThumbnail2()).isNotNull();
//    }
//
//    @Test
//    void shouldNotStartGameBecauseThereIsOne() {
//        UserPrincipal userPrincipal = new UserPrincipal(
//                userService.createUser(
//                        "email@email.pl",
//                        "username",
//                        "password",
//                        UserRole.ROLE_USER,
//                        Boolean.TRUE
//                ));
//
//        for (int i = 0; i < 2; i++) {
//            thumbnailRepository.save(Thumbnail.builder()
//                    .addedBy(userPrincipal.getUser())
//                    .url("thumbnail-url-" + i)
//                    .youtubeVideoId("youtube-id-" + i)
//                    .build());
//        }
//
//        String token = jwtService.createToken(userPrincipal);
//
//        webClient.post().uri("/api/v1/game/start")
//                .header("Authorization", "Bearer " + token)
//                .exchange()
//                .expectStatus().isOk();
//
//        webClient.post().uri("/api/v1/game/start")
//                .header("Authorization", "Bearer " + token)
//                .exchange()
//                .expectStatus().isBadRequest();
//    }
//
//    @Test
//    void shouldRespondWith404GivenNotExistingGameId() {
//        UserPrincipal userPrincipal = new UserPrincipal(
//                userService.createUser(
//                        "email@email.pl",
//                        "username",
//                        "password",
//                        UserRole.ROLE_USER,
//                        Boolean.TRUE
//                ));
//
//        String payload = """
//                {
//                    "winner_id": 1
//                }
//                """;
//
//        String token = jwtService.createToken(userPrincipal);
//
//        webClient.post().uri("/api/v1/game/result/121")
//                .header("Authorization", "Bearer " + token)
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(payload)
//                .exchange()
//                .expectStatus().isNotFound();
//    }

//    @Test
//    void shouldRespondWith403WhenUserWhoIsNotOwnerOfGameTriesToCommitRoundResult() {
//        UserPrincipal userPrincipal = new UserPrincipal(
//                userService.createUser(
//                        "email@email.pl",
//                        "username",
//                        "password",
//                        UserRole.ROLE_USER,
//                        Boolean.TRUE
//                ));
//
//        UserPrincipal otherPrincipal = new UserPrincipal(
//                userService.createUser(
//                        "email-2@email.pl",
//                        "username-2",
//                        "password",
//                        UserRole.ROLE_USER,
//                        Boolean.TRUE
//                ));
//
//        for (int i = 0; i < 2; i++) {
//            thumbnailRepository.save(Thumbnail.builder()
//                    .addedBy(userPrincipal.getUser())
//                    .url("thumbnail-url-" + i)
//                    .youtubeVideoId("youtube-id-" + i)
//                    .build());
//        }
//
//        GameResponse gameResponse = gameService.start(userPrincipal);
//
//        String token = jwtService.createToken(otherPrincipal);
//
//        String payload = """
//                {
//                    "winner_id": 1
//                }
//                """;
//
//        webClient.post().uri("/api/v1/game/round-result/" + gameResponse.getId())
//                .header("Authorization", "Bearer " + token)
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(payload)
//                .exchange()
//                .expectStatus().isForbidden();
//    }
//
//    @Test
//    void shouldRespondWith409WhenGameHasEndedWhenTryingToCommitRoundResult() {
//        UserPrincipal userPrincipal = new UserPrincipal(
//                userService.createUser(
//                        "email@email.pl",
//                        "username",
//                        "password",
//                        UserRole.ROLE_USER,
//                        Boolean.TRUE
//                ));
//
//        for (int i = 0; i < 2; i++) {
//            thumbnailRepository.save(Thumbnail.builder()
//                    .addedBy(userPrincipal.getUser())
//                    .url("thumbnail-url-" + i)
//                    .youtubeVideoId("youtube-id-" + i)
//                    .build());
//        }
//
//        GameResponse gameResponse = gameService.start(userPrincipal);
//
//        Game game = gameRepository.findById(gameResponse.getId()).orElseThrow();
//        game.setLastActivity(LocalDateTime.now(clock).minusDays(1));
//
//        gameRepository.save(game);
//
//        String token = jwtService.createToken(userPrincipal);
//
//        String payload = """
//                {
//                    "winner_id": 1
//                }
//                """;
//
//        webClient.post().uri("/api/v1/game/round-result/" + gameResponse.getId())
//                .header("Authorization", "Bearer " + token)
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(payload)
//                .exchange()
//                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
//    }

//    @Test
//    void shouldRespondWith400GivenInvalidWinnerIdWhenTryingTo() {
//        UserPrincipal userPrincipal = new UserPrincipal(
//                userService.createUser(
//                        "email@email.pl",
//                        "username",
//                        "password",
//                        UserRole.ROLE_USER,
//                        Boolean.TRUE
//                ));
//
//        for (int i = 0; i < 2; i++) {
//            thumbnailRepository.save(Thumbnail.builder()
//                    .addedBy(userPrincipal.getUser())
//                    .url("thumbnail-url-" + i)
//                    .youtubeVideoId("youtube-id-" + i)
//                    .build());
//        }
//
//        GameResponse gameResponse = gameService.start(userPrincipal);
//
//        String token = jwtService.createToken(userPrincipal);
//
//        String payload = """
//                {
//                    "winner_id": %d
//                }
//                """;
//        payload = String.format(payload, gameResponse.getThumbnail1().getId().intValue() + gameResponse.getThumbnail2().getId().intValue());
//
//        webClient.post().uri("/api/v1/game/round-result/" + gameResponse.getId())
//                .header("Authorization", "Bearer " + token)
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(payload)
//                .exchange()
//                .expectStatus().isBadRequest();
//    }

//    @Test
//    void shouldCommitRoundResult() {
//        UserPrincipal userPrincipal = new UserPrincipal(
//                userService.createUser(
//                        "email@email.pl",
//                        "username",
//                        "password",
//                        UserRole.ROLE_USER,
//                        Boolean.TRUE
//                ));
//
//        for (int i = 0; i < 3; i++) {
//            thumbnailRepository.save(Thumbnail.builder()
//                    .addedBy(userPrincipal.getUser())
//                    .url("thumbnail-url-" + i)
//                    .youtubeVideoId("youtube-id-" + i)
//                    .build());
//        }
//
//        GameResponse gameResponse = gameService.start(userPrincipal);
//        Game game = gameRepository.findById(gameResponse.getId()).orElseThrow();
//
//        String token = jwtService.createToken(userPrincipal);
//
//        String payload = """
//                {
//                    "winner_id": %d
//                }
//                """;
//        payload = String.format(payload, gameResponse.getThumbnail1().getId());
//
//        webClient.post().uri("/api/v1/game/round-result/" + gameResponse.getId())
//                .header("Authorization", "Bearer " + token)
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(payload)
//                .exchange()
//                .expectStatus().isOk();
//
//        Game updatedGame = gameRepository.findById(gameResponse.getId()).orElseThrow();
//
//        assertThat(updatedGame.getThumbnail1().getId())
//                .isEqualTo(gameResponse.getThumbnail1().getId());
//
//        assertThat(updatedGame.getThumbnail2().getId())
//                .isNotEqualTo(gameResponse.getThumbnail2().getId());
//
//        Rating rating1 = ratingRepository.findByThumbnailAndUser(
//                game.getThumbnail1(), userPrincipal.getUser()).orElseThrow();
//        Rating rating2 = ratingRepository.findByThumbnailAndUser(
//                game.getThumbnail2(), userPrincipal.getUser()).orElseThrow();
//
//        assertThat(rating1.getPoints()).isGreaterThan(ratingProperties.getBasePoints());
//        assertThat(rating2.getPoints()).isLessThan(ratingProperties.getBasePoints());
//    }

    @Test
    void shouldRespondWith404WhenTryingToEndNotExistingGame() {
        UserPrincipal userPrincipal = new UserPrincipal(
                userService.createUser(
                        "email@email.pl",
                        "username",
                        "password",
                        UserRole.ROLE_USER,
                        Boolean.TRUE
                ));

        String token = jwtService.createToken(userPrincipal);

        webClient.post().uri("/api/v1/game/end/121")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isNotFound();
    }

//    @Test
//    void shouldRespondWith403WhenUserTriesToEndNotHisGame() {
//        UserPrincipal userPrincipal = new UserPrincipal(
//                userService.createUser(
//                        "email@email.pl",
//                        "username",
//                        "password",
//                        UserRole.ROLE_USER,
//                        Boolean.TRUE
//                ));
//
//        UserPrincipal otherPrincipal = new UserPrincipal(
//                userService.createUser(
//                        "email-2@email.pl",
//                        "username-2",
//                        "password",
//                        UserRole.ROLE_USER,
//                        Boolean.TRUE
//                ));
//
//        for (int i = 0; i < 2; i++) {
//            thumbnailRepository.save(Thumbnail.builder()
//                    .addedBy(userPrincipal.getUser())
//                    .url("thumbnail-url-" + i)
//                    .youtubeVideoId("youtube-id-" + i)
//                    .build());
//        }
//
//        GameResponse gameResponse = gameService.start(userPrincipal);
//
//        String token = jwtService.createToken(otherPrincipal);
//
//        webClient.post().uri("/api/v1/game/end/" + gameResponse.getId())
//                .header("Authorization", "Bearer " + token)
//                .exchange()
//                .expectStatus().isForbidden();
//    }

    @Test
    void shouldRespondWith400WhenTryingToEndAlreadyEndedGame() {
        UserPrincipal userPrincipal = new UserPrincipal(
                userService.createUser(
                        "email@email.pl",
                        "username",
                        "password",
                        UserRole.ROLE_USER,
                        Boolean.TRUE
                ));

        List<Thumbnail> thumbnails = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            Thumbnail thumbnail = thumbnailRepository.save(Thumbnail.builder()
                    .addedBy(userPrincipal.getUser())
                    .url("thumbnail-url-" + i)
                    .youtubeVideoId("youtube-id-" + i)
                    .build());

            thumbnails.add(thumbnail);
        }

        Game game = gameRepository.save(Game.builder()
                .hasEnded(Boolean.TRUE)
                .user(userPrincipal.getUser())
                .lastActivity(LocalDateTime.now(clock))
                .thumbnail1(thumbnails.get(0))
                .thumbnail2(thumbnails.get(1))
                .build());

        String token = jwtService.createToken(userPrincipal);

        webClient.post().uri("/api/v1/game/end/" + game.getId())
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldEndGame() {
        UserPrincipal userPrincipal = new UserPrincipal(
                userService.createUser(
                        "email@email.pl",
                        "username",
                        "password",
                        UserRole.ROLE_USER,
                        Boolean.TRUE
                ));

        List<Thumbnail> thumbnails = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            Thumbnail thumbnail = thumbnailRepository.save(Thumbnail.builder()
                    .addedBy(userPrincipal.getUser())
                    .url("thumbnail-url-" + i)
                    .youtubeVideoId("youtube-id-" + i)
                    .build());

            thumbnails.add(thumbnail);
        }

        Game game = gameRepository.save(Game.builder()
                .user(userPrincipal.getUser())
                .lastActivity(LocalDateTime.now(clock))
                .thumbnail1(thumbnails.get(0))
                .thumbnail2(thumbnails.get(1))
                .build());

        String token = jwtService.createToken(userPrincipal);

        webClient.post().uri("/api/v1/game/end/" + game.getId())
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("success");

        game = gameRepository.findById(game.getId()).orElseThrow();

        assertThat(game.getHasEnded()).isTrue();
    }

    @Test
    void shouldRespondWith404WhenGettingActualActiveGameForUserWhoHasZeroNotEndedGames() {
        UserPrincipal userPrincipal = new UserPrincipal(
                userService.createUser(
                        "email@email.pl",
                        "username",
                        "password",
                        UserRole.ROLE_USER,
                        Boolean.TRUE
                ));

        List<Thumbnail> thumbnails = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            Thumbnail thumbnail = thumbnailRepository.save(Thumbnail.builder()
                    .addedBy(userPrincipal.getUser())
                    .url("thumbnail-url-" + i)
                    .youtubeVideoId("youtube-id-" + i)
                    .build());

            thumbnails.add(thumbnail);
        }

        gameRepository.save(Game.builder()
                .hasEnded(Boolean.TRUE)
                .user(userPrincipal.getUser())
                .lastActivity(LocalDateTime.now(clock))
                .thumbnail1(thumbnails.get(0))
                .thumbnail2(thumbnails.get(1))
                .hasEnded(Boolean.TRUE)
                .build());

        String token = jwtService.createToken(userPrincipal);

        webClient.get().uri("/api/v1/game")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldRespondWith404WhenGettingActualActiveGameForUserWhoHasNotEndedGamesButTheyAreExpired() {
        UserPrincipal userPrincipal = new UserPrincipal(
                userService.createUser(
                        "email@email.pl",
                        "username",
                        "password",
                        UserRole.ROLE_USER,
                        Boolean.TRUE
                ));

        List<Thumbnail> thumbnails = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            Thumbnail thumbnail = thumbnailRepository.save(Thumbnail.builder()
                    .addedBy(userPrincipal.getUser())
                    .url("thumbnail-url-" + i)
                    .youtubeVideoId("youtube-id-" + i)
                    .build());

            thumbnails.add(thumbnail);
        }

        gameRepository.save(Game.builder()
                .hasEnded(Boolean.TRUE)
                .user(userPrincipal.getUser())
                .lastActivity(LocalDateTime.now(clock).minusDays(10))
                .thumbnail1(thumbnails.get(0))
                .thumbnail2(thumbnails.get(1))
                .build());

        String token = jwtService.createToken(userPrincipal);

        webClient.get().uri("/api/v1/game")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldGetActualActiveGame() {
        UserPrincipal userPrincipal = new UserPrincipal(
                userService.createUser(
                        "email@email.pl",
                        "username",
                        "password",
                        UserRole.ROLE_USER,
                        Boolean.TRUE
                ));

        List<Thumbnail> thumbnails = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            Thumbnail thumbnail = thumbnailRepository.save(Thumbnail.builder()
                    .addedBy(userPrincipal.getUser())
                    .url("thumbnail-url-" + i)
                    .youtubeVideoId("youtube-id-" + i)
                    .build());

            thumbnails.add(thumbnail);
        }

        Game game = gameRepository.save(Game.builder()
                .user(userPrincipal.getUser())
                .lastActivity(LocalDateTime.now(clock))
                .thumbnail1(thumbnails.get(0))
                .thumbnail2(thumbnails.get(1))
                .build());

        String token = jwtService.createToken(userPrincipal);

        webClient.get().uri("/api/v1/game")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(game.getId())
                .jsonPath("$.thumbnail1.id").isEqualTo(game.getThumbnail1().getId())
                .jsonPath("$.thumbnail2.id").isEqualTo(game.getThumbnail2().getId());
    }

    private static User createUser() {
        return User.builder()
                .email("email@email.pl")
                .username("username")
                .password("password")
                .userRole(UserRole.ROLE_USER)
                .isEnabled(true)
                .build();
    }

    private static Thumbnail createThumbnail(String url, String ytId, User user) {
        return Thumbnail
                .builder()
                .url(url)
                .youtubeVideoId(ytId)
                .addedBy(user)
                .build();
    }

    private static Rating createRating(User user, Thumbnail thumbnail, double points) {
        return Rating.builder()
                .user(user)
                .thumbnail(thumbnail)
                .points(BigDecimal.valueOf(points))
                .build();
    }
}
